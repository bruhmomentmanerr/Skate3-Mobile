package chat.buku.skate3;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Second-screen companion for dual-screen handhelds such as the AYN Thor.
 *
 * It shows a trick reference, live controller presets and a few settings,
 * and gives one-tap Pause / recomp menu / Back buttons. The window is not
 * focusable, so the built-in gamepad keeps talking to the game on the top
 * screen; touches still work.
 */
final class BottomScreenPanel extends Presentation {
    private static final String TAG = "Skate3Panel";
    private static final int START = 0x0010;
    private static final int BACK = 0x0020;
    private static final int RB = 0x0200;
    private static final int ORANGE = Color.rgb(255, 104, 24);
    private static final int CARD = Color.rgb(32, 32, 38);
    private static final int MUTED = Color.rgb(160, 160, 168);

    private enum Tab { TRICKS, CONTROLS, SETUP }

    interface Host {
        TouchControllerView touchController();
        void onPanelTurnedOff();
    }

    private final Host host;
    private final DeviceProfiles.Recommendation device;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Tab tab = Tab.TRICKS;
    private boolean pulsing;
    private boolean dimmed;
    private LinearLayout root;
    private TextView status;
    private LinearLayout tabs;
    private LinearLayout body;

    BottomScreenPanel(Context context, Display display, Host host,
                      DeviceProfiles.Recommendation device) {
        super(context, display, android.R.style.Theme_DeviceDefault_NoActionBar);
        this.host = host;
        this.device = device;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            // Keep input focus on the game display. Without this, touching
            // the bottom screen can move focus here and the gamepad stops
            // reaching SDL on the top screen.
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(12, 12, 15));
        root.setPadding(dp(14), dp(12), dp(14), dp(12));

        TextView title = text("SKATE 3 MOBILE", 13, ORANGE);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setLetterSpacing(0.08f);
        root.addView(title, wrap(dp(2)));

        status = text("", 12, MUTED);
        root.addView(status, wrap(dp(10)));

        tabs = new LinearLayout(getContext());
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        root.addView(tabs, fixed(dp(46), dp(10)));

        ScrollView scroll = new ScrollView(getContext());
        scroll.setFillViewport(true);
        body = new LinearLayout(getContext());
        body.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(body, new ScrollView.LayoutParams(
            ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        scrollParams.bottomMargin = dp(10);
        root.addView(scroll, scrollParams);

        LinearLayout quick = new LinearLayout(getContext());
        quick.setOrientation(LinearLayout.HORIZONTAL);
        quick.addView(quickButton("PAUSE", v -> pulse(START, 0)), weighted(dp(6)));
        quick.addView(quickButton("RECOMP MENU", v -> pulse(RB, START)), weighted(dp(6)));
        quick.addView(quickButton("BACK", v -> pulse(BACK, 0)), weighted(0));
        root.addView(quick, fixed(dp(64), 0));

        setContentView(root);
        refresh();
    }

    @Override
    protected void onStop() {
        handler.removeCallbacksAndMessages(null);
        pulsing = false;
        TouchControllerBridge.setPanelButtons(0);
        super.onStop();
    }

    // The window is not focusable, so these should never fire. If a vendor
    // build routes gamepad input here anyway, hand it straight back to the game.
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Activity owner = getOwnerActivity();
        return owner != null ? owner.dispatchKeyEvent(event) : super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        Activity owner = getOwnerActivity();
        return owner != null ? owner.dispatchGenericMotionEvent(event)
                             : super.dispatchGenericMotionEvent(event);
    }

    void refresh() {
        if (root == null) return;
        status.setText(device.label + "  •  " +
                       localize(HandheldSettings.graphics(getContext()).label) + "\n" +
                       localize("Controls") + ": " +
                       localize(ControllerPresets.active().label));
        tabs.removeAllViews();
        tabs.addView(tabButton("TRICKS", Tab.TRICKS), weighted(dp(6)));
        tabs.addView(tabButton("CONTROLS", Tab.CONTROLS), weighted(dp(6)));
        tabs.addView(tabButton("SETUP", Tab.SETUP), weighted(0));
        body.removeAllViews();
        switch (tab) {
            case TRICKS: buildTricks(); break;
            case CONTROLS: buildControls(); break;
            case SETUP: buildSetup(); break;
        }
    }

    private void buildTricks() {
        JSONObject list = loadTricks();
        if (list == null) {
            body.addView(text("The trick list could not be loaded.", 14, Color.WHITE), wrap(0));
            return;
        }
        JSONArray sections = list.optJSONArray("sections");
        for (int s = 0; sections != null && s < sections.length(); ++s) {
            JSONObject section = sections.optJSONObject(s);
            if (section == null) continue;
            TextView heading = text(section.optString("title"), 12, ORANGE);
            heading.setTypeface(Typeface.DEFAULT_BOLD);
            body.addView(heading, wrap(dp(6)));
            JSONArray rows = section.optJSONArray("rows");
            for (int r = 0; rows != null && r < rows.length(); ++r) {
                JSONArray row = rows.optJSONArray(r);
                if (row == null || row.length() < 2) continue;
                LinearLayout card = card();
                TextView name = text(row.optString(0), 15, Color.WHITE);
                name.setTypeface(Typeface.DEFAULT_BOLD);
                card.addView(name, wrap(dp(2)));
                card.addView(text(row.optString(1), 13, Color.rgb(210, 210, 216)), wrap(0));
                body.addView(card, wrap(dp(6)));
            }
            body.addView(new View(getContext()), fixed(dp(6), 0));
        }
        String footer = list.optString("footer");
        if (!footer.isEmpty()) body.addView(text(footer, 12, MUTED), wrap(0));
    }

    private void buildControls() {
        body.addView(text("Applies instantly to the built-in controls and any connected gamepad.",
                          13, MUTED), wrap(dp(10)));
        ControllerPresets.Preset active = ControllerPresets.active();
        for (ControllerPresets.Preset preset : ControllerPresets.Preset.values()) {
            boolean selected = preset == active;
            Button choice = button(localize(preset.label) + (selected ? "  •  " + localize("SELECTED") : ""),
                                   selected);
            choice.setOnClickListener(v -> {
                ControllerPresets.apply(preset);
                HandheldSettings.setController(getContext(), preset);
                refresh();
            });
            body.addView(choice, fixed(dp(52), dp(4)));
            if (selected) {
                body.addView(text(preset.description, 13, Color.rgb(210, 210, 216)), wrap(dp(10)));
            } else {
                body.addView(new View(getContext()), fixed(dp(6), 0));
            }
        }
    }

    private void buildSetup() {
        TouchControllerView touch = host.touchController();
        boolean touchVisible = touch != null && touch.areControlsVisible();
        Button touchToggle = button(localize("TOUCH CONTROLS ON GAME SCREEN") + ": " +
                                    localize(touchVisible ? "ON" : "OFF"), false);
        touchToggle.setOnClickListener(v -> {
            if (touch != null) touch.showControls(!touch.areControlsVisible());
            refresh();
        });
        body.addView(touchToggle, fixed(dp(52), dp(8)));

        boolean keepToggle = HandheldSettings.topTouchToggleWithPanel(getContext());
        Button toggleButton = button(localize("TOUCH BUTTON ON GAME SCREEN") + ": " +
                                     localize(keepToggle ? "SHOWN" : "HIDDEN"), false);
        toggleButton.setOnClickListener(v -> {
            boolean next = !HandheldSettings.topTouchToggleWithPanel(getContext());
            HandheldSettings.setTopTouchToggleWithPanel(getContext(), next);
            if (touch != null) touch.setToggleHidden(!next);
            refresh();
        });
        body.addView(toggleButton, fixed(dp(52), dp(8)));

        Button dim = button(localize("DIM THIS SCREEN") + ": " + localize(dimmed ? "ON" : "OFF"), false);
        dim.setOnClickListener(v -> {
            dimmed = !dimmed;
            root.setAlpha(dimmed ? 0.35f : 1f);
            refresh();
        });
        body.addView(dim, fixed(dp(52), dp(8)));

        Button off = button(localize("CLOSE PANEL UNTIL NEXT LAUNCH"), false);
        off.setOnClickListener(v -> host.onPanelTurnedOff());
        body.addView(off, fixed(dp(52), dp(14)));

        HandheldSettings.Graphics graphics = HandheldSettings.graphics(getContext());
        String recommended = device.tier == null
            ? localize("No device-specific recommendation; Auto keeps the memory-based default.")
            : localize("Auto picks") + ": " + localize(tierLabel(device.tier));
        LinearLayout info = card();
        TextView heading = text("Graphics", 14, Color.WHITE);
        heading.setTypeface(Typeface.DEFAULT_BOLD);
        info.addView(heading, wrap(dp(4)));
        info.addView(text(localize(graphics.label) + "\n" + recommended + "\n\n" +
                          localize("Graphics changes need a restart. Change them in the launcher under HANDHELD SETUP."),
                          13, Color.rgb(210, 210, 216)), wrap(0));
        body.addView(info, wrap(0));
    }

    /**
     * Presses buttons on the shared virtual pad for a moment. For a chord the
     * second button lands 60 ms after the first, like a person pressing
     * RB then Start, which is what the recomp menu shortcut expects.
     */
    private void pulse(int first, int second) {
        if (pulsing) return;
        pulsing = true;
        TouchControllerBridge.setPanelButtons(first);
        if (second != 0) {
            handler.postDelayed(() -> TouchControllerBridge.setPanelButtons(first | second), 60);
        }
        handler.postDelayed(() -> {
            TouchControllerBridge.setPanelButtons(0);
            pulsing = false;
        }, second != 0 ? 260 : 160);
    }

    private JSONObject loadTricks() {
        try (InputStream in = getContext().getAssets().open("panel/tricks.json")) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) > 0) out.write(buffer, 0, read);
            return new JSONObject(new String(out.toByteArray(), StandardCharsets.UTF_8));
        } catch (Exception exception) {
            Log.w(TAG, "Could not read panel/tricks.json", exception);
            return null;
        }
    }

    private static String tierLabel(DeviceProfiles.Tier tier) {
        switch (tier) {
            case PERFORMANCE: return HandheldSettings.Graphics.PERFORMANCE.label;
            case BALANCED: return HandheldSettings.Graphics.BALANCED.label;
            default: return HandheldSettings.Graphics.QUALITY.label;
        }
    }

    private Button tabButton(String label, Tab target) {
        Button button = button(localize(label), tab == target);
        button.setOnClickListener(v -> {
            tab = target;
            refresh();
        });
        return button;
    }

    private Button quickButton(String label, View.OnClickListener listener) {
        Button button = button(localize(label), true);
        button.setTextSize(14);
        button.setOnClickListener(listener);
        return button;
    }

    private Button button(String label, boolean primary) {
        Button button = new Button(getContext());
        button.setAllCaps(false);
        button.setText(label);
        button.setTextSize(13);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setTextColor(primary ? Color.BLACK : Color.WHITE);
        button.setBackgroundTintList(ColorStateList.valueOf(primary ? ORANGE : Color.rgb(48, 48, 54)));
        button.setFocusable(false);
        return button;
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(CARD);
        card.setPadding(dp(12), dp(10), dp(12), dp(10));
        return card;
    }

    private TextView text(String value, int size, int color) {
        TextView text = new TextView(getContext());
        text.setText(localize(value));
        text.setTextSize(size);
        text.setTextColor(color);
        text.setLineSpacing(0, 1.1f);
        text.setGravity(Gravity.START);
        return text;
    }

    private String localize(String english) {
        return LauncherStrings.text(getContext(), english);
    }

    private LinearLayout.LayoutParams wrap(int bottomMargin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = bottomMargin;
        return params;
    }

    private LinearLayout.LayoutParams fixed(int height, int bottomMargin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, height);
        params.bottomMargin = bottomMargin;
        return params;
    }

    private LinearLayout.LayoutParams weighted(int endMargin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        params.setMarginEnd(endMargin);
        return params;
    }

    private int dp(int value) {
        return Math.round(value * getContext().getResources().getDisplayMetrics().density);
    }
}
