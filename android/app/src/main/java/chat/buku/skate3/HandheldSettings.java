package chat.buku.skate3;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Handheld options shared by the launcher, the game activity and the
 * bottom-screen panel. All three run in the same process, so one
 * SharedPreferences file is enough and changes are visible immediately.
 */
final class HandheldSettings {
    private static final String PREFS = "skate3_handheld";
    private static final String KEY_GRAPHICS = "graphics_preset";
    private static final String KEY_CONTROLLER = "controller_preset";
    private static final String KEY_PANEL = "bottom_panel";
    private static final String KEY_TOP_TOGGLE = "top_touch_toggle_with_panel";

    /** Graphics presets, passed to native code as SKATE3_GRAPHICS_PRESET. */
    enum Graphics {
        AUTO("auto", "Auto (recommended for this device)"),
        PERFORMANCE("performance", "Performance (288p, coolest)"),
        BALANCED("balanced", "Balanced (540p, cooler than Quality)"),
        QUALITY("quality", "Quality (720p, full materials)");

        final String id;
        final String label;

        Graphics(String id, String label) {
            this.id = id;
            this.label = label;
        }

        static Graphics fromId(String id) {
            for (Graphics graphics : values()) {
                if (graphics.id.equals(id)) return graphics;
            }
            return AUTO;
        }
    }

    /** When the bottom-screen panel appears. */
    enum Panel {
        AUTO("auto", "Auto (dual-screen handhelds only)"),
        ON("on", "On whenever a second screen exists"),
        OFF("off", "Off");

        final String id;
        final String label;

        Panel(String id, String label) {
            this.id = id;
            this.label = label;
        }

        static Panel fromId(String id) {
            for (Panel panel : values()) {
                if (panel.id.equals(id)) return panel;
            }
            return AUTO;
        }
    }

    private HandheldSettings() {}

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    static Graphics graphics(Context context) {
        return Graphics.fromId(prefs(context).getString(KEY_GRAPHICS, Graphics.AUTO.id));
    }

    static void setGraphics(Context context, Graphics graphics) {
        prefs(context).edit().putString(KEY_GRAPHICS, graphics.id).apply();
    }

    static ControllerPresets.Preset controller(Context context) {
        return ControllerPresets.Preset.fromId(
            prefs(context).getString(KEY_CONTROLLER, ControllerPresets.Preset.STANDARD.id));
    }

    static void setController(Context context, ControllerPresets.Preset preset) {
        prefs(context).edit().putString(KEY_CONTROLLER, preset.id).apply();
    }

    static Panel panel(Context context) {
        return Panel.fromId(prefs(context).getString(KEY_PANEL, Panel.AUTO.id));
    }

    static void setPanel(Context context, Panel panel) {
        prefs(context).edit().putString(KEY_PANEL, panel.id).apply();
    }

    /** Whether the top screen keeps its TOUCH button while the panel is open. */
    static boolean topTouchToggleWithPanel(Context context) {
        return prefs(context).getBoolean(KEY_TOP_TOGGLE, false);
    }

    static void setTopTouchToggleWithPanel(Context context, boolean visible) {
        prefs(context).edit().putBoolean(KEY_TOP_TOGGLE, visible).apply();
    }

    /** One line for bug reports. */
    static String describe(Context context) {
        return "graphics=" + graphics(context).id +
            ", controller=" + controller(context).id +
            ", panel=" + panel(context).id;
    }
}
