package com.example.ui.common

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticFeedbackHelper(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun performHaptic(isPowerButton: Boolean = false, enabled: Boolean = true) {
        if (!enabled) return
        try {
            val vib = vibrator ?: return
            if (!vib.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val effect = if (isPowerButton) {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                } else {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                }
                vib.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                val ms = if (isPowerButton) 50L else 20L
                @Suppress("DEPRECATION")
                vib.vibrate(ms)
            }
        } catch (e: Exception) {
            android.util.Log.e("HapticFeedbackHelper", "Failed to perform haptic feedback", e)
        }
    }
}
