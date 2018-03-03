package fr.aquillet.kiwi.jna.callback;

import java.util.concurrent.TimeUnit;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;

import fr.aquillet.kiwi.jna.event.INativeEvent;
import fr.aquillet.kiwi.jna.event.MouseEvent;
import fr.aquillet.kiwi.jna.event.MouseEventType;
import fr.aquillet.kiwi.jna.event.MouseScrollEvent;
import fr.aquillet.kiwi.jna.struct.MSLLHOOKSTRUCT;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultLowLevelMouseProc implements LowLevelMouseProc {

	private static final int WM_MOUSEMOVE = 512;
	private static final int WM_MOUSESCROLL = 522;
	private static final int WM_MOUSELDOWN = 513;
	private static final int WM_MOUSELUP = 514;
	private static final int WM_MOUSEMDOWN = 519;
	private static final int WM_MOUSEMUP = 520;
	private static final int WM_MOUSERDOWN = 516;
	private static final int WM_MOUSERUP = 517;

	private PublishSubject<INativeEvent> eventsSubject = PublishSubject.create();
	private PublishSubject<INativeEvent> moveEventsSubject = PublishSubject.create();

	@Override
	public LRESULT callback(int nCode, WPARAM wParam, MSLLHOOKSTRUCT info) {
		if (nCode >= 0) {
			switch (wParam.intValue()) {
			case WM_MOUSELDOWN:
			case WM_MOUSELUP:
			case WM_MOUSEMDOWN:
			case WM_MOUSEMUP:
			case WM_MOUSERDOWN:
			case WM_MOUSERUP:
				eventsSubject.onNext(MouseEvent.builder() //
						.eventType(MouseEventType.fromNativeEventType(wParam.intValue())) //
						.x(info.pt.x) //
						.y(info.pt.y) //
						.flags(info.flags.intValue()) //
						.build());
				break;
			case WM_MOUSESCROLL:
				eventsSubject.onNext(MouseScrollEvent.builder() //
						.scroll(info.mouseData.getHigh().intValue()) //
						.build());
				break;
			case WM_MOUSEMOVE:
				moveEventsSubject.onNext(MouseEvent.builder() //
						.eventType(MouseEventType.fromNativeEventType(wParam.intValue())) //
						.x(info.pt.x) //
						.y(info.pt.y) //
						.flags(info.flags.intValue()) //
						.build());
				break;
			default:
				log.warn("Unmanaged mouse event type (type: {})", wParam.intValue());
				break;
			}

		}

		Pointer ptr = info.getPointer();
		long peer = Pointer.nativeValue(ptr);
		return User32.INSTANCE.CallNextHookEx(null, nCode, wParam, new LPARAM(peer));
	}

	public Observable<INativeEvent> getEventsStream() {
		return eventsSubject.mergeWith(moveEventsSubject //
				.throttleLast(10, TimeUnit.MILLISECONDS));
	}

}
