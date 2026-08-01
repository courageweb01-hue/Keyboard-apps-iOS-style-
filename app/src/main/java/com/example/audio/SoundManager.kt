package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundManager(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_SYSTEM, 70)
        } catch (e: Exception) {
            toneGenerator = null
        }
    }

    fun playKeyPressSound(soundStyle: String, volume: Float = 0.7f, pitch: Float = 1.0f) {
        if (soundStyle == "SILENT") return

        try {
            val toneType = when (soundStyle) {
                "MECHANICAL" -> ToneGenerator.TONE_PROP_BEEP
                "BUBBLE" -> ToneGenerator.TONE_DTMF_0
                "SOFT_TAP" -> ToneGenerator.TONE_PROP_PROMPT
                "CYBER_PULSE" -> ToneGenerator.TONE_CDMA_PIP
                "TYPEWRITER" -> ToneGenerator.TONE_PROP_ACK
                else -> ToneGenerator.TONE_PROP_BEEP
            }
            toneGenerator?.startTone(toneType, 20)
        } catch (_: Exception) {
            // Fallback to standard system key click if tone generator fails
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, volume)
        }
    }

    fun performHapticFeedback(enabled: Boolean) {
        if (!enabled || vibrator == null) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(12)
            }
        } catch (_: Exception) {
            // Ignore if vibration fails on device without motor
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
