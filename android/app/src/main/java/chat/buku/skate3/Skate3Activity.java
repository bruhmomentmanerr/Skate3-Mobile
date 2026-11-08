package chat.buku.skate3;

import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import org.libsdl.app.SDLActivity;
import org.libsdl.app.SDLControllerManager;

import java.io.File;

public class Skate3Activity extends SDLActivity implements BottomScreenPanel.Host {
    private static final String INPUT_TAG = "Skate3Input";
    private static final String PANEL_TAG = "Skate3Panel";
    private static volatile boolean sessionActive;
    private TouchControllerView touchController;
    private DeviceProfiles.Recommendation device;
    private DisplayManager displayManager;
    private BottomScreenPanel panel;
    private boolean panelClosedThisSession;
    private boolean resumed;

    private final DisplayManager.DisplayListener displayListener =
        new DisplayManager.DisplayListener() {
            @Override
            public void onDisplayAdded(int displayId) {
                if (resumed) showPanelIfWanted();
            }

            @Override
            public void onDisplayRemoved(int displayId) {
                if (panel != null && panel.getDisplay().getDisplayId() == displayId) {
                    dismissPanel();
                }
            }

            @Override
            public void onDisplayChanged(int displayId) {}
        };

    static boolean isSessionActive() {
        return sessionActive;
    }

    @Override
    protected String[] getLibraries() {
        return new String[] { "c++_shared", "rexruntime", "skate3" };
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        sessionActive = true;
        String files = getFilesDir().getAbsolutePath();
        nativeSetenv("XDG_DATA_HOME", files);
        nativeSetenv("HOME", files);
        nativeSetenv("SKATE3_INTERNAL_FILES_DIR", files);
        nativeSetenv("SKATE3_NATIVE_LIBRARY_DIR", getApplicationInfo().nativeLibraryDir);
        CustomGpuDriver.Driver driver = CustomGpuDriver.installed(this);
        if (driver != null && driver.enabled) {
            nativeSetenv("SKATE3_VULKAN_DRIVER_DIR",
                         driver.directory.toAbsolutePath().toString() + File.separator);
            nativeSetenv("SKATE3_VULKAN_DRIVER_NAME", driver.libraryName);
            Log.i("Skate3GpuDriver", "Selected custom driver: " + driver.label());
        } else {
            nativeSetenv("SKATE3_VULKAN_DRIVER_DIR", "");
            nativeSetenv("SKATE3_VULKAN_DRIVER_NAME", "");
            Log.i("Skate3GpuDriver", "Selected system driver");
        }

        // Handheld presets. Native code reads these while it configures
        // paths, before the renderer starts (see skate3_app_common.cpp).
        device = DeviceProfiles.current();
        HandheldSettings.Graphics graphics = HandheldSettings.graphics(this);
        nativeSetenv("SKATE3_GRAPHICS_PRESET", graphics.id);
        nativeSetenv("SKATE3_GRAPHICS_RECOMMENDED", device.nativeId());
        nativeSetenv("SKATE3_DEVICE_LABEL", device.label);
        ControllerPresets.apply(HandheldSettings.controller(this));
        Log.i(INPUT_TAG, "Device " + device.label + "; " + HandheldSettings.describe(this) +
              "; recommended=" + device.nativeId());

        touchController = new TouchControllerView(this);
        mLayout.addView(touchController, new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        displayManager = getSystemService(DisplayManager.class);
        if (displayManager != null) {
            displayManager.registerDisplayListener(displayListener, null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;
        showPanelIfWanted();
    }

    @Override
    protected void onPause() {
        resumed = false;
        if (touchController != null) touchController.clearInput();
        dismissPanel();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        sessionActive = false;
        if (displayManager != null) displayManager.unregisterDisplayListener(displayListener);
        dismissPanel();
        if (touchController != null) touchController.disconnect();
        super.onDestroy();
    }

    // BottomScreenPanel.Host
    @Override
    public TouchControllerView touchController() {
        return touchController;
    }

    @Override
    public void onPanelTurnedOff() {
        panelClosedThisSession = true;
        dismissPanel();
    }

    private void showPanelIfWanted() {
        if (panel != null || panelClosedThisSession || device == null) return;
        Display target = findPanelDisplay();
        if (target == null) return;
        try {
            BottomScreenPanel created = new BottomScreenPanel(this, target, this, device);
            created.setOwnerActivity(this);
            created.show();
            panel = created;
            if (touchController != null) {
                touchController.setToggleHidden(!HandheldSettings.topTouchToggleWithPanel(this));
            }
            Log.i(PANEL_TAG, "Panel shown on display " + target.getDisplayId() +
                  " (" + target.getName() + ")");
        } catch (RuntimeException exception) {
            // WindowManager.InvalidDisplayException and friends: the display
            // went away or does not accept presentations. Play on without it.
            Log.w(PANEL_TAG, "Could not show the bottom-screen panel", exception);
        }
    }

    private void dismissPanel() {
        if (panel == null) return;
        try {
            panel.dismiss();
        } catch (RuntimeException ignored) {
            // The display may already be gone.
        }
        panel = null;
        if (touchController != null) touchController.setToggleHidden(false);
    }

    private Display findPanelDisplay() {
        HandheldSettings.Panel mode = HandheldSettings.panel(this);
        if (mode == HandheldSettings.Panel.OFF) return null;
        if (mode == HandheldSettings.Panel.AUTO && !device.dualScreenHandheld) return null;
        if (displayManager == null) return null;
        Display own = getDisplay();
        int ownId = own != null ? own.getDisplayId() : Display.DEFAULT_DISPLAY;
        for (Display display :
                displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)) {
            if (display.getDisplayId() != ownId && display.isValid()) return display;
        }
        return null;
    }

    private static int getAllSources(int deviceId, int eventSource) {
        InputDevice device = InputDevice.getDevice(deviceId);
        return device == null ? eventSource : eventSource | device.getSources();
    }

    private static boolean isControllerEvent(int deviceId, int eventSource) {
        int sources = getAllSources(deviceId, eventSource);
        return (sources & (InputDevice.SOURCE_GAMEPAD |
                           InputDevice.SOURCE_JOYSTICK |
                           InputDevice.SOURCE_DPAD)) != 0;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int deviceId = event.getDeviceId();
        if (isControllerEvent(deviceId, event.getSource())) {
            boolean handled = false;
            int keyCode = event.getKeyCode();
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                keyCode = ControllerPresets.remapDown(deviceId, keyCode);
                handled = SDLControllerManager.onNativePadDown(deviceId, keyCode);
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                keyCode = ControllerPresets.remapUp(deviceId, keyCode);
                handled = SDLControllerManager.onNativePadUp(deviceId, keyCode);
            }
            Log.i(INPUT_TAG, "key action=" + event.getAction() +
                    " code=" + event.getKeyCode() + " sent=" + keyCode + " device=" + deviceId +
                    " eventSources=0x" + Integer.toHexString(event.getSource()) +
                    " allSources=0x" + Integer.toHexString(getAllSources(deviceId, event.getSource())) +
                    " handled=" + handled);
            if (handled) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (isControllerEvent(event.getDeviceId(), event.getSource()) &&
                SDLControllerManager.handleJoystickMotionEvent(event)) {
            return true;
        }
        return super.dispatchGenericMotionEvent(event);
    }

}
