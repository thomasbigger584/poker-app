package com.twb.pokerapp.ui.util;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

/**
 * Small wrapper around the platform {@link Vibrator} so game code can fire short,
 * intentional buzzes without repeating the API-level plumbing everywhere.
 */
public final class HapticUtil {

    /** Two quick taps — used to alert the player that it is their turn to act. */
    private static final long[] YOUR_TURN_PATTERN = {0, 60, 90, 120};
    private static final int[] YOUR_TURN_AMPLITUDES = {0, 180, 0, 255};

    private HapticUtil() {
    }

    /**
     * Buzzes a distinctive "it's your turn" pattern. No-ops on devices without a vibrator.
     */
    public static void yourTurn(Context context) {
        var vibrator = getVibrator(context);
        if (vibrator == null || !vibrator.hasVibrator()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(YOUR_TURN_PATTERN, YOUR_TURN_AMPLITUDES, -1));
        } else {
            vibrator.vibrate(YOUR_TURN_PATTERN, -1);
        }
    }

    private static Vibrator getVibrator(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            var manager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            return (manager == null) ? null : manager.getDefaultVibrator();
        }
        return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }
}
