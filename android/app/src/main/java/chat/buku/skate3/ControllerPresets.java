package chat.buku.skate3;

import android.view.KeyEvent;

import org.libsdl.app.SDLControllerManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller mapping presets for physical and built-in gamepads.
 *
 * Remapping happens before events reach SDL, so the game still sees a normal
 * Xbox pad. Face buttons are remapped per key event here; the stick swap is
 * applied inside SDL's joystick handler, which reads
 * {@link SDLControllerManager#setSwapSticks(boolean)}.
 */
final class ControllerPresets {
    enum Preset {
        STANDARD("standard", "Standard",
                 "Buttons and sticks exactly as labelled on an Xbox-layout pad.",
                 false, false),
        NINTENDO("nintendo", "Nintendo-style face buttons",
                 "Swaps A with B and X with Y. Use this if your handheld is set to a Nintendo layout, or if A on your device should act as B.",
                 true, false),
        SOUTHPAW("southpaw", "Southpaw (sticks swapped)",
                 "The left stick does Flick-It board tricks and the right stick steers.",
                 false, true),
        SOUTHPAW_NINTENDO("southpaw_nintendo", "Southpaw + Nintendo-style",
                 "Swapped sticks and swapped face buttons together.",
                 true, true);

        final String id;
        final String label;
        final String description;
        final boolean swapFaceButtons;
        final boolean swapSticks;

        Preset(String id, String label, String description,
               boolean swapFaceButtons, boolean swapSticks) {
            this.id = id;
            this.label = label;
            this.description = description;
            this.swapFaceButtons = swapFaceButtons;
            this.swapSticks = swapSticks;
        }

        static Preset fromId(String id) {
            for (Preset preset : values()) {
                if (preset.id.equals(id)) return preset;
            }
            return STANDARD;
        }
    }

    private static volatile Preset active = Preset.STANDARD;

    /**
     * The key code each held button was sent as. A release must go out as the
     * same code its press did, even if the preset changed in between;
     * otherwise a swapped button stays held in game.
     */
    private static final Map<Long, Integer> heldAs = new HashMap<>();

    private ControllerPresets() {}

    static Preset active() {
        return active;
    }

    static void apply(Preset preset) {
        active = preset;
        SDLControllerManager.setSwapSticks(preset.swapSticks);
    }

    /** Returns the key code to send to SDL for a controller key press. */
    static synchronized int remapDown(int deviceId, int keyCode) {
        int mapped = active.swapFaceButtons ? swapFace(keyCode) : keyCode;
        heldAs.put(key(deviceId, keyCode), mapped);
        return mapped;
    }

    /** Returns the key code to send to SDL for a controller key release. */
    static synchronized int remapUp(int deviceId, int keyCode) {
        Integer mapped = heldAs.remove(key(deviceId, keyCode));
        if (mapped != null) return mapped;
        return active.swapFaceButtons ? swapFace(keyCode) : keyCode;
    }

    static synchronized void forgetHeldButtons() {
        heldAs.clear();
    }

    static int swapFace(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_A: return KeyEvent.KEYCODE_BUTTON_B;
            case KeyEvent.KEYCODE_BUTTON_B: return KeyEvent.KEYCODE_BUTTON_A;
            case KeyEvent.KEYCODE_BUTTON_X: return KeyEvent.KEYCODE_BUTTON_Y;
            case KeyEvent.KEYCODE_BUTTON_Y: return KeyEvent.KEYCODE_BUTTON_X;
            default: return keyCode;
        }
    }

    private static long key(int deviceId, int keyCode) {
        return ((long) deviceId << 32) | (keyCode & 0xffffffffL);
    }
}
