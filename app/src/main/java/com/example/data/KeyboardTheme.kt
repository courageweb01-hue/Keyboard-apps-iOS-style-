package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keyboard_themes")
data class KeyboardTheme(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isPreset: Boolean = false,
    val glassColorHex: String = "#331E293B", // Translucent slate
    val borderGlowColorHex: String = "#FF38BDF8", // Cyan glow border
    val keyBgColorHex: String = "#22FFFFFF", // Frosted translucent white
    val keyTextColorHex: String = "#FFFFFFFF",
    val accentColorHex: String = "#FF818CF8", // Indigo accent
    val glassOpacity: Float = 0.45f,
    val blurRadiusDp: Float = 16f,
    val cornerRadiusDp: Float = 12f,
    val glowIntensity: Float = 0.8f,
    val soundStyle: String = "MECHANICAL", // MECHANICAL, BUBBLE, SOFT_TAP, CYBER_PULSE, TYPEWRITER, SILENT
    val soundVolume: Float = 0.7f,
    val soundPitch: Float = 1.0f,
    val hapticEnabled: Boolean = true,
    val particleEffect: String = "NEON_SPARKLES", // NONE, NEON_SPARKLES, GLASS_SHARDS, GLOW_ORBS
    val keyHeightScale: Float = 1.0f
) {
    companion object {
        val PRESETS = listOf(
            KeyboardTheme(
                id = 1,
                name = "Frosted Acrylic",
                isPreset = true,
                glassColorHex = "#250F172A",
                borderGlowColorHex = "#8038BDF8",
                keyBgColorHex = "#26FFFFFF",
                keyTextColorHex = "#F8FAFC",
                accentColorHex = "#38BDF8",
                glassOpacity = 0.35f,
                blurRadiusDp = 20f,
                cornerRadiusDp = 10f,
                glowIntensity = 0.6f,
                soundStyle = "MECHANICAL",
                particleEffect = "NEON_SPARKLES"
            ),
            KeyboardTheme(
                id = 2,
                name = "Cyber Neon",
                isPreset = true,
                glassColorHex = "#30030712",
                borderGlowColorHex = "#FFEC4899",
                keyBgColorHex = "#30EC4899",
                keyTextColorHex = "#F472B6",
                accentColorHex = "#A855F7",
                glassOpacity = 0.50f,
                blurRadiusDp = 12f,
                cornerRadiusDp = 14f,
                glowIntensity = 1.0f,
                soundStyle = "CYBER_PULSE",
                particleEffect = "GLOW_ORBS"
            ),
            KeyboardTheme(
                id = 3,
                name = "Glacier Ice",
                isPreset = true,
                glassColorHex = "#20082F49",
                borderGlowColorHex = "#FF06B6D4",
                keyBgColorHex = "#25E0F2FE",
                keyTextColorHex = "#E0F2FE",
                accentColorHex = "#0284C7",
                glassOpacity = 0.30f,
                blurRadiusDp = 24f,
                cornerRadiusDp = 16f,
                glowIntensity = 0.7f,
                soundStyle = "SOFT_TAP",
                particleEffect = "GLASS_SHARDS"
            ),
            KeyboardTheme(
                id = 4,
                name = "Emerald Prism",
                isPreset = true,
                glassColorHex = "#25022C22",
                borderGlowColorHex = "#FF10B981",
                keyBgColorHex = "#25D1FAE5",
                keyTextColorHex = "#ECFDF5",
                accentColorHex = "#059669",
                glassOpacity = 0.40f,
                blurRadiusDp = 18f,
                cornerRadiusDp = 12f,
                glowIntensity = 0.8f,
                soundStyle = "BUBBLE",
                particleEffect = "NEON_SPARKLES"
            ),
            KeyboardTheme(
                id = 5,
                name = "Rose Gold Glass",
                isPreset = true,
                glassColorHex = "#252A1215",
                borderGlowColorHex = "#FFFB7185",
                keyBgColorHex = "#25FFE4E6",
                keyTextColorHex = "#FFF1F2",
                accentColorHex = "#F43F5E",
                glassOpacity = 0.40f,
                blurRadiusDp = 16f,
                cornerRadiusDp = 18f,
                glowIntensity = 0.75f,
                soundStyle = "TYPEWRITER",
                particleEffect = "GLOW_ORBS"
            ),
            KeyboardTheme(
                id = 6,
                name = "Deep Midnight",
                isPreset = true,
                glassColorHex = "#40030712",
                borderGlowColorHex = "#FF6366F1",
                keyBgColorHex = "#1A6366F1",
                keyTextColorHex = "#E0E7FF",
                accentColorHex = "#4F46E5",
                glassOpacity = 0.60f,
                blurRadiusDp = 10f,
                cornerRadiusDp = 8f,
                glowIntensity = 0.5f,
                soundStyle = "MECHANICAL",
                particleEffect = "NONE"
            )
        )
    }
}
