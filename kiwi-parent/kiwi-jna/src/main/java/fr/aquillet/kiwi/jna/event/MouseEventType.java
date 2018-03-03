package fr.aquillet.kiwi.jna.event;

public enum MouseEventType {

    MOUSE_LEFT_UP(514), // WM_MOUSELUP
    MOUSE_LEFT_DOWN(513), // WM_MOUSELDOWN
    MOUSE_RIGHT_UP(517), // WM_MOUSERUP
    MOUSE_RIGHT_DOWN(516), // WM_MOUSERDOWN
    MOUSE_MIDDLE_UP(520), // WM_MOUSEMUP
    MOUSE_MIDDLE_DOWN(519), // WM_MOUSEMDOWN
    MOUSE_MOVE(512), // WM_MOUSEMOVE
    MOUSE_SCROLL(522); // WM_MOUSESCROLL

    private final int type;

    MouseEventType(int type) {
        this.type = type;
    }

    public static MouseEventType fromNativeEventType(int type) {
        switch (type) {
            case 514:
                return MOUSE_LEFT_UP;
            case 513:
                return MOUSE_LEFT_DOWN;
            case 517:
                return MOUSE_RIGHT_UP;
            case 516:
                return MOUSE_RIGHT_DOWN;
            case 520:
                return MOUSE_MIDDLE_UP;
            case 519:
                return MOUSE_MIDDLE_DOWN;
            case 512:
                return MOUSE_MOVE;
            case 522:
                return MOUSE_SCROLL;
            default:
                throw new RuntimeException("Unmanaged native type: " + type);
        }
    }

    public int getNativeType() {
        return type;
    }

}
