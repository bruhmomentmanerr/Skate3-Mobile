package chat.buku.skate3;

/**
 * The one virtual pad native code knows about, fed by two sources: the
 * on-screen touch controller and the bottom-screen panel's buttons. Native
 * code keeps a single state and the last write wins, so both sources go
 * through here and are merged before anything is sent.
 */
final class TouchControllerBridge {
    private static volatile boolean nativeAvailable = true;

    private static boolean overlayConnected;
    private static int overlayButtons;
    private static float overlayLeftX;
    private static float overlayLeftY;
    private static float overlayRightX;
    private static float overlayRightY;
    private static float overlayLeftTrigger;
    private static float overlayRightTrigger;
    private static int panelButtons;

    private TouchControllerBridge() {}

    static native void setState(int buttons,
                                float leftX, float leftY,
                                float rightX, float rightY,
                                float leftTrigger, float rightTrigger);

    /** State from the on-screen touch controller; buttons < 0 means hidden. */
    static synchronized void setOverlayState(int buttons,
                                             float leftX, float leftY,
                                             float rightX, float rightY,
                                             float leftTrigger, float rightTrigger) {
        overlayConnected = buttons >= 0;
        overlayButtons = Math.max(buttons, 0);
        overlayLeftX = leftX;
        overlayLeftY = leftY;
        overlayRightX = rightX;
        overlayRightY = rightY;
        overlayLeftTrigger = leftTrigger;
        overlayRightTrigger = rightTrigger;
        push();
    }

    /** Buttons currently held by the bottom-screen panel (0 = none). */
    static synchronized void setPanelButtons(int buttons) {
        panelButtons = Math.max(buttons, 0);
        push();
    }

    private static void push() {
        if (!overlayConnected && panelButtons == 0) {
            trySetState(-1, 0, 0, 0, 0, 0, 0);
            return;
        }
        if (!overlayConnected) {
            trySetState(panelButtons, 0, 0, 0, 0, 0, 0);
            return;
        }
        trySetState(overlayButtons | panelButtons,
                    overlayLeftX, overlayLeftY, overlayRightX, overlayRightY,
                    overlayLeftTrigger, overlayRightTrigger);
    }

    private static void trySetState(int buttons,
                                    float leftX, float leftY,
                                    float rightX, float rightY,
                                    float leftTrigger, float rightTrigger) {
        if (!nativeAvailable) return;
        try {
            setState(buttons, leftX, leftY, rightX, rightY,
                     leftTrigger, rightTrigger);
        } catch (UnsatisfiedLinkError error) {
            nativeAvailable = false;
        }
    }
}
