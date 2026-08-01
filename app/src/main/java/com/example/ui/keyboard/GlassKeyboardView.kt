package com.example.ui.keyboard

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.audio.SoundManager
import com.example.data.KeyboardTheme
import com.example.data.QuickPhrase

@Composable
fun GlassKeyboardView(
    theme: KeyboardTheme = KeyboardTheme.PRESETS[0],
    quickPhrases: List<QuickPhrase> = QuickPhrase.DEFAULT_PHRASES,
    onKeyPress: (String) -> Unit = {},
    onBackspace: () -> Unit = {},
    onEnter: () -> Unit = {},
    onSpace: () -> Unit = {},
    onPhraseSelect: (QuickPhrase) -> Unit = {}
) {
    val context = LocalContext.current
    val soundManager = remember(context) { SoundManager(context) }

    DisposableEffect(context) {
        onDispose {
            soundManager.release()
        }
    }

    var activeMode by remember { mutableStateOf(KeyboardMode.ALPHA) }
    var shiftState by remember { mutableStateOf(ShiftState.OFF) }

    // Particle burst state
    var particlePosition by remember { mutableStateOf<Offset?>(null) }

    val glassBgColor = remember(theme) {
        try {
            Color(android.graphics.Color.parseColor(theme.glassColorHex))
        } catch (_: Exception) {
            Color(0xFF0F172A).copy(alpha = 0.85f)
        }
    }

    val glowBorder = remember(theme) {
        try {
            Color(android.graphics.Color.parseColor(theme.borderGlowColorHex)).copy(alpha = theme.glowIntensity)
        } catch (_: Exception) {
            Color(0xFF38BDF8)
        }
    }

    val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

    fun handlePress(position: Offset, action: () -> Unit) {
        particlePosition = position
        soundManager.playKeyPressSound(theme.soundStyle, theme.soundVolume, theme.soundPitch)
        soundManager.performHapticFeedback(theme.hapticEnabled)
        action()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glassBgColor.copy(alpha = (theme.glassOpacity + 0.3f).coerceAtMost(0.95f)),
                        glassBgColor.copy(alpha = (theme.glassOpacity + 0.1f).coerceAtMost(0.85f))
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(glowBorder, Color.Transparent)
                ),
                shape = shape
            )
            .padding(top = 6.dp, bottom = 8.dp, start = 4.dp, end = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Mode Header Strip
            KeyboardTopBar(
                activeMode = activeMode,
                onModeChange = { activeMode = it },
                quickPhrases = quickPhrases,
                onQuickPhraseClick = { phraseText ->
                    onKeyPress(phraseText)
                },
                theme = theme
            )

            // Active Keyboard Layout
            when (activeMode) {
                KeyboardMode.ALPHA -> {
                    QwertyLayout(
                        shiftState = shiftState,
                        theme = theme,
                        onKeyPress = { key, pos ->
                            handlePress(pos) {
                                onKeyPress(key)
                                if (shiftState == ShiftState.ON) {
                                    shiftState = ShiftState.OFF
                                }
                            }
                        },
                        onBackspace = { pos -> handlePress(pos) { onBackspace() } },
                        onEnter = { pos -> handlePress(pos) { onEnter() } },
                        onSpace = { pos -> handlePress(pos) { onSpace() } },
                        onShiftToggle = {
                            shiftState = when (shiftState) {
                                ShiftState.OFF -> ShiftState.ON
                                ShiftState.ON -> ShiftState.CAPS_LOCK
                                ShiftState.CAPS_LOCK -> ShiftState.OFF
                            }
                        },
                        onModeToggle = { activeMode = it }
                    )
                }

                KeyboardMode.NUMBERS -> {
                    SymbolsNumbersLayout(
                        isSymbols = false,
                        theme = theme,
                        onKeyPress = { symbol, pos -> handlePress(pos) { onKeyPress(symbol) } },
                        onBackspace = { pos -> handlePress(pos) { onBackspace() } },
                        onEnter = { pos -> handlePress(pos) { onEnter() } },
                        onSpace = { pos -> handlePress(pos) { onSpace() } },
                        onModeToggle = { activeMode = it }
                    )
                }

                KeyboardMode.SYMBOLS -> {
                    SymbolsNumbersLayout(
                        isSymbols = true,
                        theme = theme,
                        onKeyPress = { symbol, pos -> handlePress(pos) { onKeyPress(symbol) } },
                        onBackspace = { pos -> handlePress(pos) { onBackspace() } },
                        onEnter = { pos -> handlePress(pos) { onEnter() } },
                        onSpace = { pos -> handlePress(pos) { onSpace() } },
                        onModeToggle = { activeMode = it }
                    )
                }

                KeyboardMode.EMOJI -> {
                    EmojiKaomojiLayout(
                        theme = theme,
                        onKeyPress = { emoji, pos -> handlePress(pos) { onKeyPress(emoji) } },
                        onBackspace = { pos -> handlePress(pos) { onBackspace() } },
                        onModeToggle = { activeMode = it }
                    )
                }

                KeyboardMode.QUICK_PHRASES -> {
                    QuickPhrasesLayout(
                        phrases = quickPhrases,
                        theme = theme,
                        onPhraseSelect = { phrase ->
                            onPhraseSelect(phrase)
                            onKeyPress(phrase.text)
                        },
                        onModeToggle = { activeMode = it }
                    )
                }
            }
        }

        // Floating particles layer
        particlePosition?.let { pos ->
            ParticleBurst(
                position = pos,
                color = glowBorder,
                particleType = theme.particleEffect,
                onComplete = { particlePosition = null }
            )
        }
    }
}
