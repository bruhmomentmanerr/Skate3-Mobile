# Handheld setup: Thor panel, controller layouts, graphics presets

Fork additions aimed at dual-screen and high-end Android handhelds, with the
AYN Thor as the target. Everything is opt-out and leaves unknown devices on
the existing behaviour.

## What's new

### HANDHELD SETUP in the launcher

A new button under **PLAY SKATE 3** opens one screen with three choices:

| Setting | Options | Applies |
| --- | --- | --- |
| Graphics preset | Auto, Performance (288p), Balanced (540p), Quality (720p) | Next launch |
| Controller layout | Standard, Nintendo-style face buttons, Southpaw, Southpaw + Nintendo | Immediately |
| Bottom screen panel | Auto (dual-screen handhelds), On, Off | Next launch |

### Graphics presets

- **Auto** keeps the in-game **RB + Start > Video > Android Device Profile**
  in charge. The device table in `DeviceProfiles.java` only changes the
  *starting* profile, before saved settings load:
  - Snapdragon 8 Gen 1 / 8+ Gen 1 / 8 Gen 2 (incl. the Thor's QCS8550) /
    8s Gen 3 / 8 Gen 3 / 8 Elite: Quality.
  - Snapdragon 865/870, 888, 7+ Gen 2, 7 Gen 3, 7s Gen 3, 7+ Gen 3:
    Quality profile at the Balanced 540-line tier.
  - Anything else: no change (the existing memory rule decides).
- **Performance / Balanced / Quality** are locked choices. They override the
  in-game profile at every launch until you go back to Auto.
- **Balanced** is new: the High-End material pipeline with a 960x540 scene
  target (720x540 at 4:3), 0.8x world/LOD distance, MSAA off, and no SSAO,
  light shafts or PCSS shadows. It exists because 720p Quality runs warm on
  handhelds.

Plumbing: `Skate3Activity` sets `SKATE3_GRAPHICS_PRESET`,
`SKATE3_GRAPHICS_RECOMMENDED` and `SKATE3_DEVICE_LABEL`.
`skate3_android_defaults.cpp` uses the recommendation for the starting
profile; `skate3_app_common.cpp` applies the preset after saved settings and
sets the new `skate3_android_scene_tier` cvar, which
`EnsureOutputSizedTargets` in `skate3_native_scene_gpu.cpp` reads.

### Controller layouts

Remapped in Java before SDL sees the event, so the game still gets a normal
Xbox pad.

- Face-button swap: `ControllerPresets.remapDown/remapUp` in
  `Skate3Activity.dispatchKeyEvent`. A release always goes out as the same
  code as its press, so changing the layout mid-press cannot leave a button
  stuck.
- Stick swap: `SDLJoystickHandler.handleMotionEvent` trades axis slots 0/1
  with 2/3 (left and right stick in SDL's default Android mapping). Triggers
  are untouched.

### Bottom-screen panel

An Android `Presentation` on the second display, shown while the game is in
the foreground:

- **TRICKS**: a reference card loaded from
  `android/app/src/main/assets/panel/tricks.json`. It only lists inputs that
  sources agree on; edit the JSON to extend or correct it.
- **CONTROLS**: switch controller layouts live.
- **SETUP**: touch controls on or off, show or hide the TOUCH button on the
  game screen, dim the panel, close it for the session, and the current
  graphics preset.
- **PAUSE / RECOMP MENU / BACK** quick buttons. They press Start, RB+Start
  (with a 60 ms stagger) and Back on the shared virtual pad.

The panel window is `FLAG_NOT_FOCUSABLE` so the built-in gamepad keeps
driving the game. While it is open, the TOUCH button on the game screen is
hidden by default (see issue #136); the panel's SETUP tab can bring it back.

`TouchControllerBridge` now merges the touch overlay and the panel into the
one virtual pad native code knows about. Before, the last writer won.

Bug reports gain a `Handheld setup:` line with the active choices.

## Not verified yet

None of this has run on hardware. Before a release, check on an AYN Thor:

1. The bottom screen is listed under `DISPLAY_CATEGORY_PRESENTATION`. If it
   is not, the panel silently does not appear (logcat tag `Skate3Panel`) and
   would need a secondary-display Activity instead.
2. Touching the panel does not take gamepad focus from the game.
3. RECOMP MENU opens the RB + Start overlay; PAUSE and BACK work in menus and
   in gameplay.
4. Southpaw swaps the sticks on the Thor's built-in pad (the stick swap
   assumes SDL's default Android axis order; a gamecontrollerdb entry for the
   pad would change that).
5. Balanced renders at 540 lines and is visibly cooler than Quality over a
   15-minute session.
6. Auto on the Thor starts on Quality on a fresh install.

Known cosmetic gap: the in-game **3D Scene Resolution** row is drawn by the
runtime (`third_party/rexglue-sdk`) and still shows 1280x720 when Balanced is
active.
