package fr.aquillet.kiwi.jna.event;

import com.sun.jna.platform.win32.WinUser;

public enum KeyboardEventType {

	KEY_UP(WinUser.WM_KEYUP), //
	KEY_DOWN(WinUser.WM_KEYDOWN), //
	SYSTEM_KEY_UP(WinUser.WM_SYSKEYUP), //
	SYSTEM_KEY_DOWN(WinUser.WM_SYSKEYDOWN); //

	public static KeyboardEventType fromNativeEventType(int type) {
		switch (type) {
		case WinUser.WM_KEYUP:
			return KEY_UP;
		case WinUser.WM_KEYDOWN:
			return KEY_DOWN;
		case WinUser.WM_SYSKEYUP:
			return SYSTEM_KEY_UP;
		case WinUser.WM_SYSKEYDOWN:
			return SYSTEM_KEY_DOWN;
		default:
			throw new RuntimeException("Unmanaged native type: " + type);
		}
	}

	private final int type;

	private KeyboardEventType(int type) {
		this.type = type;
	}

	public int getNativeType() {
		return type;
	}

}
