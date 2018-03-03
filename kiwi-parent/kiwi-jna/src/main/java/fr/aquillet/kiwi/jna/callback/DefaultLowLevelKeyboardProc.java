package fr.aquillet.kiwi.jna.callback;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;

import fr.aquillet.kiwi.jna.event.INativeEvent;
import fr.aquillet.kiwi.jna.event.KeyboardEvent;
import fr.aquillet.kiwi.jna.event.KeyboardEventType;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultLowLevelKeyboardProc implements LowLevelKeyboardProc {

	private PublishSubject<INativeEvent> eventsSubject = PublishSubject.create();

	@Override
	public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) {
		if (nCode >= 0) {
			switch (wParam.intValue()) {
			case WinUser.WM_KEYUP:
			case WinUser.WM_KEYDOWN:
			case WinUser.WM_SYSKEYUP:
			case WinUser.WM_SYSKEYDOWN:
				eventsSubject.onNext(KeyboardEvent.builder() //
						.eventType(KeyboardEventType.fromNativeEventType(wParam.intValue())) //
						.virtualKeyCode(info.vkCode) //
						.scanCode(info.scanCode) //
						.flags(info.flags) //
						.build());
				break;
			default:
				log.warn("Unmanaged keyboard event type (type: {})", wParam.intValue());
				break;
			}

		}

		Pointer ptr = info.getPointer();
		long peer = Pointer.nativeValue(ptr);
		return User32.INSTANCE.CallNextHookEx(null, nCode, wParam, new LPARAM(peer));
	}

	public Observable<INativeEvent> getEventsStream() {
		return eventsSubject;
	}

}
