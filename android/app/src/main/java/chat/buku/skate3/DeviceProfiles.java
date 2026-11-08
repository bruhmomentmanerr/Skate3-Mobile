package chat.buku.skate3;

import android.os.Build;

import java.util.Locale;

/**
 * Per-device recommendations for the Auto graphics preset and the
 * bottom-screen panel.
 *
 * Detection is by SoC first, because the GPU decides what a profile costs,
 * and by maker/model only for features that are about the hardware shape
 * (the AYN Thor's second screen). Unknown devices get no recommendation and
 * keep the native code's existing RAM-based default, so nothing changes for
 * hardware this table has never seen.
 *
 * The logic lives in {@link #recommend} with plain string inputs so it can be
 * unit tested off-device.
 */
final class DeviceProfiles {
    enum Tier { PERFORMANCE, BALANCED, QUALITY }

    static final class Recommendation {
        /** Null when the device is unknown; native code then decides by RAM. */
        final Tier tier;
        final String label;
        final boolean dualScreenHandheld;

        Recommendation(Tier tier, String label, boolean dualScreenHandheld) {
            this.tier = tier;
            this.label = label;
            this.dualScreenHandheld = dualScreenHandheld;
        }

        /** Value for SKATE3_GRAPHICS_RECOMMENDED; empty means "no opinion". */
        String nativeId() {
            if (tier == null) return "";
            return tier.name().toLowerCase(Locale.ROOT);
        }
    }

    private DeviceProfiles() {}

    static Recommendation current() {
        String soc = Build.VERSION.SDK_INT >= 31 ? Build.SOC_MODEL : "";
        return recommend(Build.MANUFACTURER, Build.MODEL, soc, Build.HARDWARE);
    }

    static Recommendation recommend(String manufacturer, String model, String socModel,
                                    String hardware) {
        String maker = clean(manufacturer);
        String name = clean(model);
        String soc = clean(socModel);
        if (soc.isEmpty()) soc = clean(hardware);

        boolean thor = maker.contains("AYN") && name.contains("THOR");
        String chip = chipName(soc);
        Tier tier = socTier(soc);

        StringBuilder label = new StringBuilder();
        if (thor) {
            label.append("AYN Thor");
        } else if (!maker.isEmpty() || !name.isEmpty()) {
            label.append(pretty(manufacturer)).append(' ').append(pretty(model));
        } else {
            label.append("Unknown device");
        }
        if (chip != null) label.append(" (").append(chip).append(')');
        return new Recommendation(tier, label.toString().trim(), thor);
    }

    /**
     * GPU class by Qualcomm part number. Only chips with a known class are
     * listed; everything else returns null and keeps today's behaviour.
     */
    static Tier socTier(String soc) {
        switch (soc) {
            // Adreno 730/740/750/830 class: the RP6 integration ran Quality on
            // SM8550, the same chip as the AYN Thor and Odin 2.
            case "SM8450":   // Snapdragon 8 Gen 1
            case "SM8475":   // Snapdragon 8+ Gen 1
            case "SM8550":   // Snapdragon 8 Gen 2
            case "QCS8550":  // Snapdragon 8 Gen 2 (handheld/IoT SKU used by AYN)
            case "SM8635":   // Snapdragon 8s Gen 3
            case "SM8650":   // Snapdragon 8 Gen 3
            case "SM8750":   // Snapdragon 8 Elite
                return Tier.QUALITY;
            // Adreno 650/660 and upper-midrange 7-series: 720p is likely to
            // run warm; 540p keeps the full material pipeline.
            case "SM8250":   // Snapdragon 865 / 870
            case "SM8350":   // Snapdragon 888
            case "SM7475":   // Snapdragon 7+ Gen 2
            case "SM7550":   // Snapdragon 7 Gen 3
            case "SM7635":   // Snapdragon 7s Gen 3
            case "SM7675":   // Snapdragon 7+ Gen 3
                return Tier.BALANCED;
            default:
                return null;
        }
    }

    static String chipName(String soc) {
        switch (soc) {
            case "SM8450": return "Snapdragon 8 Gen 1";
            case "SM8475": return "Snapdragon 8+ Gen 1";
            case "SM8550":
            case "QCS8550": return "Snapdragon 8 Gen 2";
            case "SM8635": return "Snapdragon 8s Gen 3";
            case "SM8650": return "Snapdragon 8 Gen 3";
            case "SM8750": return "Snapdragon 8 Elite";
            case "SM8250": return "Snapdragon 865/870";
            case "SM8350": return "Snapdragon 888";
            case "SM7475": return "Snapdragon 7+ Gen 2";
            case "SM7550": return "Snapdragon 7 Gen 3";
            case "SM7635": return "Snapdragon 7s Gen 3";
            case "SM7675": return "Snapdragon 7+ Gen 3";
            default: return soc.isEmpty() ? null : soc;
        }
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private static String pretty(String value) {
        return value == null ? "" : value.trim();
    }
}
