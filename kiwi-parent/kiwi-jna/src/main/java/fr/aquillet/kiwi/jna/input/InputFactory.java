package fr.aquillet.kiwi.jna.input;

import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinUser.INPUT;
import fr.aquillet.kiwi.jna.event.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class InputFactory {

    private static final int MOUSEEVENTF_MOVE = 1;
    private static final int MOUSEEVENTF_LEFTDOWN = 2;
    private static final int MOUSEEVENTF_LEFTUP = 4;
    private static final int MOUSEEVENTF_RIGHTDOWN = 8;
    private static final int MOUSEEVENTF_RIGHTUP = 16;
    private static final int MOUSEEVENTF_MIDDLEDOWN = 32;
    private static final int MOUSEEVENTF_MIDDLEUP = 64;
    private static final int MOUSEEVENTF_WHEEL = 2048;
    //	public static final long MOUSEEVENTF_VIRTUALDESK = 0x4000L;
    private static final long MOUSEEVENTF_ABSOLUTE = 0x8000L;
    private static final int KEYEVENTF_KEYDOWN = 0;
    private static final int KEYEVENTF_KEYUP = 2;

    private InputFactory() {
        // utility class
    }

    public static Optional<List<Runnable>> from(INativeEvent event) {
        if (event instanceof KeyboardEvent) {
            return Optional.of(from((KeyboardEvent) event));
        }
        if (event instanceof MouseEvent) {
            return Optional.of(from((MouseEvent) event));
        }
        if (event instanceof MouseScrollEvent) {
            return Optional.of(from((MouseScrollEvent) event));
        }
        if (event instanceof PauseEvent) {
            return Optional.of(Collections.emptyList());
        }

        return Optional.empty();
    }

    private static List<Runnable> from(KeyboardEvent event) {
        INPUT input = new INPUT();
        input.type = new DWORD(INPUT.INPUT_KEYBOARD);
        input.input.setType("ki");
        input.input.ki.wScan = new WORD(0);
        input.input.ki.time = new DWORD(0);
        input.input.ki.dwExtraInfo = new ULONG_PTR(0);
        input.input.ki.wVk = new WORD(event.getVirtualKeyCode());
        input.input.ki.dwFlags = getKeyTypeFromEvent(event);
        return Collections.singletonList(() -> User32.INSTANCE.SendInput(new DWORD(1), (INPUT[]) input.toArray(1), input.size()));
    }

    private static List<Runnable> from(MouseEvent event) {
        INPUT inputMove = new INPUT();
        inputMove.type = new DWORD(INPUT.INPUT_MOUSE);
        inputMove.input.setType("mi");
        inputMove.input.mi.dx = new LONG(Math.round(event.getX() * 65535D / User32.INSTANCE.GetSystemMetrics(User32.SM_CXSCREEN)));
        inputMove.input.mi.dy = new LONG(Math.round((event.getY() + 1) * 65535D / User32.INSTANCE.GetSystemMetrics(User32.SM_CYSCREEN)));
        inputMove.input.mi.mouseData = new DWORD(0);
        inputMove.input.mi.time = new DWORD(0);
        inputMove.input.mi.dwExtraInfo = new ULONG_PTR(0);
        inputMove.input.mi.dwFlags = new DWORD(MOUSEEVENTF_MOVE | MOUSEEVENTF_ABSOLUTE);

        INPUT inputEvent = new INPUT();
        inputEvent.type = new DWORD(INPUT.INPUT_MOUSE);
        inputEvent.input.setType("mi");
        inputEvent.input.mi.time = new DWORD(0);
        inputEvent.input.mi.dwExtraInfo = new ULONG_PTR(0);
        inputEvent.input.mi.dwFlags = getMouseTypeFromEvent(event);

        List<Runnable> actions = new ArrayList<>();
        actions.add(() -> User32.INSTANCE.SetCursorPos(event.getX(), event.getY()));
        actions.add(() -> User32.INSTANCE.SendInput(new DWORD(1), (INPUT[]) inputEvent.toArray(1), inputEvent.size()));
        return actions;
    }

    private static List<Runnable> from(MouseScrollEvent event) {
        INPUT input = new INPUT();
        input.type = new DWORD(INPUT.INPUT_MOUSE);
        input.input.setType("mi");
        input.input.mi.mouseData = new DWORD(event.getScroll());
        input.input.mi.dx = new LONG(0);
        input.input.mi.dy = new LONG(0);
        input.input.mi.time = new DWORD(0);
        input.input.mi.dwExtraInfo = new ULONG_PTR(0);
        input.input.mi.dwFlags = new DWORD(MOUSEEVENTF_WHEEL);
        return Collections.singletonList(() -> User32.INSTANCE.SendInput(new DWORD(1), (INPUT[]) input.toArray(1), input.size()));
    }

    private static DWORD getKeyTypeFromEvent(KeyboardEvent event) {
        return Optional.of(event) //
                .filter(e -> KeyboardEventType.KEY_UP.equals(e.getEventType()) //
                        || KeyboardEventType.SYSTEM_KEY_UP.equals(e.getEventType()))
                .map(any -> new DWORD(KEYEVENTF_KEYUP)) //
                .orElse(new DWORD(KEYEVENTF_KEYDOWN));
    }

    private static DWORD getMouseTypeFromEvent(MouseEvent event) {
        switch (event.getEventType()) {
            case MOUSE_LEFT_DOWN:
                return new DWORD(MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_ABSOLUTE);
            case MOUSE_LEFT_UP:
                return new DWORD(MOUSEEVENTF_LEFTUP | MOUSEEVENTF_ABSOLUTE);
            case MOUSE_MIDDLE_DOWN:
                return new DWORD(MOUSEEVENTF_MIDDLEDOWN | MOUSEEVENTF_ABSOLUTE);
            case MOUSE_MIDDLE_UP:
                return new DWORD(MOUSEEVENTF_MIDDLEUP | MOUSEEVENTF_ABSOLUTE);
            case MOUSE_RIGHT_DOWN:
                return new DWORD(MOUSEEVENTF_RIGHTDOWN | MOUSEEVENTF_ABSOLUTE);
            case MOUSE_RIGHT_UP:
                return new DWORD(MOUSEEVENTF_RIGHTUP | MOUSEEVENTF_ABSOLUTE);
            case MOUSE_MOVE:
                return new DWORD(MOUSEEVENTF_MOVE | MOUSEEVENTF_ABSOLUTE);
            default:
                throw new RuntimeException();
        }
    }

}
