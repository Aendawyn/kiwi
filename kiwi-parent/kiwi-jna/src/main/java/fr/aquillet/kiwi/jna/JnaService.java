package fr.aquillet.kiwi.jna;

import com.sun.jna.Native;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.MSG;
import fr.aquillet.kiwi.jna.callback.DefaultLowLevelKeyboardProc;
import fr.aquillet.kiwi.jna.callback.DefaultLowLevelMouseProc;
import fr.aquillet.kiwi.jna.event.INativeEvent;
import fr.aquillet.kiwi.jna.input.InputFactory;
import fr.aquillet.kiwi.toolkit.rx.RxUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class JnaService {

    private static final int MAX_TITLE_LENGTH = 1024;

    private DefaultLowLevelKeyboardProc keyboardCallback = new DefaultLowLevelKeyboardProc();
    private DefaultLowLevelMouseProc mouseCallback = new DefaultLowLevelMouseProc();

    private HHOOK keyboardHook;
    private HHOOK mouseHook;

    public JnaService() {
        initialize();
    }

    public void shutDown() {
        log.info("Uninstalling keyboard and mouse hooks.");
        User32.INSTANCE.UnhookWindowsHookEx(keyboardHook);
        User32.INSTANCE.UnhookWindowsHookEx(mouseHook);
    }

    public Observable<Process> runApplication(String command, String workingDirectory, int delaySec) {
        return Observable.defer(() -> {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File(workingDirectory));
            return Observable.just(builder.start()).delay(delaySec, TimeUnit.SECONDS);
        }).observeOn(Schedulers.io());
    }

    public Completable sendNativeEventinput(INativeEvent event) {
        return Completable.defer(() -> InputFactory.from(event) //
                .map(inputs -> inputs.stream() //
                        .map(Observable::just) //
                        .map(obs -> obs.doOnNext(Runnable::run)) //
                        .collect(Collectors.toList()))
                .map(Observable::concat) //
                .map(Observable::ignoreElements) //
                .orElseGet(Completable::complete)).observeOn(Schedulers.io());
    }

    public Completable bringApplicationForeground(String applicationTitle) {
        return Completable.defer(() -> getWindows().entrySet().stream() //
                .filter(e -> e.getValue().equals(applicationTitle)) //
                .map(Map.Entry::getKey) //
                .findFirst() //
                .map(User32.INSTANCE::SetForegroundWindow) //
                .map(result -> { //
                    if (!result) {
                        return Completable.error(new IOException(
                                "Unable to bring application '" + applicationTitle + "' to front."));
                    } else {
                        return Completable.complete();
                    }
                }).orElse(Completable
                        .error(new IOException("Unable to find application '" + applicationTitle + "'."))));
    }

    public Observable<INativeEvent> getNativeEventsStream() {
        Observable<INativeEvent> eventsStream = Observable.merge( //
                keyboardCallback.getEventsStream(), //
                mouseCallback.getEventsStream()) //
                .share();

        return Observable.zip( //
                eventsStream, eventsStream.timeInterval(), //
                (event, time) -> {
                    event.setTime(time.time(TimeUnit.MILLISECONDS));
                    return event;
                });
    }

    public List<DesktopWindow> getActiveWindows() {
        return WindowUtils.getAllWindows(true);
    }

    private void initialize() {
        Completable.defer(() -> {
            log.info("Initializing native events hooks...");
            HMODULE hModule = Kernel32.INSTANCE.GetModuleHandle(null);
            keyboardHook = User32.INSTANCE.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardCallback, hModule, 0);
            mouseHook = User32.INSTANCE.SetWindowsHookEx(WinUser.WH_MOUSE_LL, mouseCallback, hModule, 0);
            if (keyboardHook != null && mouseHook != null) {
                log.info("Keyboard and mouse hooks installed.");
            }

            int result;
            MSG message = new MSG();
            while ((result = User32.INSTANCE.GetMessage(message, null, 0, 0)) != 0) {
                if (result == -1) {
                    log.error("Error while getting native message (error: {})", Native.getLastError());
                    return Completable.error(new RuntimeException());
                } else {
                    log.debug("Dispatching native message (msg: {})", message);
                    User32.INSTANCE.TranslateMessage(message);
                    User32.INSTANCE.DispatchMessage(message);
                }
                if (Thread.interrupted()) {
                    log.warn("Daemon thread interrupted.");
                    break;
                }
            }
            return Completable.complete();
        }) //
                .subscribeOn(Schedulers.io()) //
                .observeOn(Schedulers.io()) //
                .subscribe(RxUtils.nothingToDoCompletable(), RxUtils.logError(log));
    }

    private Map<HWND, String> getWindows() {
        Map<HWND, String> map = new HashMap<>();
        User32.INSTANCE.EnumWindows((hWnd, arg1) -> {
            String wText = getWindowTitle(hWnd);
            map.put(hWnd, wText);
            return true;
        }, null);
        return map;
    }

    private String getWindowTitle(HWND hWnd) {
        return WindowUtils.getWindowTitle(hWnd);
    }

    public String getWindowClassName(HWND hWnd) {
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        User32.INSTANCE.GetClassName(hWnd, buffer, MAX_TITLE_LENGTH);
        return Native.toString(buffer);
    }

    public Rectangle getForegroundWindowBounds() {
        return WindowUtils.getWindowLocationAndSize(getActiveWindow());
    }

    private HWND getActiveWindow() {
        return User32.INSTANCE.GetForegroundWindow();
    }

    public BufferedImage getWIndowIcon(HWND hWnd) {
        return WindowUtils.getWindowIcon(hWnd);
    }
}
