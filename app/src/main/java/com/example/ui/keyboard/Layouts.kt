package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardTheme
import com.example.data.QuickPhrase

enum class KeyboardMode {
    ALPHA, NUMBERS, SYMBOLS, EMOJI, QUICK_PHRASES
}

enum class ShiftState {
    OFF, ON, CAPS_LOCK
}

@Composable
fun KeyboardTopBar(
    activeMode: KeyboardMode,
    onModeChange: (KeyboardMode) -> Unit,
    quickPhrases: List<QuickPhrase>,
    onQuickPhraseClick: (String) -> Unit,
    theme: KeyboardTheme
) {
    val accentColor = try {
        Color(android.graphics.Color.parseColor(theme.accentColorHex))
    } catch (_: Exception) {
        Color(0xFF818CF8)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Mode Selector Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ModeIconButton(
                    icon = Icons.Default.Keyboard,
                    isSelected = activeMode == KeyboardMode.ALPHA || activeMode == KeyboardMode.NUMBERS || activeMode == KeyboardMode.SYMBOLS,
                    accentColor = accentColor,
                    onClick = { onModeChange(KeyboardMode.ALPHA) }
                )
                ModeIconButton(
                    icon = Icons.Default.FlashOn,
                    isSelected = activeMode == KeyboardMode.QUICK_PHRASES,
                    accentColor = accentColor,
                    onClick = { onModeChange(KeyboardMode.QUICK_PHRASES) }
                )
                ModeIconButton(
                    icon = Icons.Default.EmojiEmotions,
                    isSelected = activeMode == KeyboardMode.EMOJI,
                    accentColor = accentColor,
                    onClick = { onModeChange(KeyboardMode.EMOJI) }
                )
            }

            // Quick Phrase Horizontal Suggestions Pill Strip
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickPhrases.take(6).forEach { phrase ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .clickable { onQuickPhraseClick(phrase.text) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = phrase.title,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) accentColor else Color.White.copy(alpha = 0.1f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Mode",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun QwertyLayout(
    shiftState: ShiftState,
    theme: KeyboardTheme,
    onKeyPress: (String, Offset) -> Unit,
    onBackspace: (Offset) -> Unit,
    onEnter: (Offset) -> Unit,
    onSpace: (Offset) -> Unit,
    onShiftToggle: () -> Unit,
    onModeToggle: (KeyboardMode) -> Unit
) {
    val row1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val row2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val row3 = listOf("z", "x", "c", "v", "b", "n", "m")

    val keyHeight = (44 * theme.keyHeightScale).dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            row1.forEach { key ->
                val text = if (shiftState != ShiftState.OFF) key.uppercase() else key
                KeyButton(
                    label = text,
                    theme = theme,
                    height = keyHeight,
                    modifier = Modifier.weight(1f),
                    onPress = { pos -> onKeyPress(text, pos) }
                )
            }
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            row2.forEach { key ->
                val text = if (shiftState != ShiftState.OFF) key.uppercase() else key
                KeyButton(
                    label = text,
                    theme = theme,
                    height = keyHeight,
                    modifier = Modifier.weight(1f),
                    onPress = { pos -> onKeyPress(text, pos) }
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        // Row 3 (Shift + Keys + Backspace)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            KeyButton(
                icon = Icons.Default.ArrowUpward,
                theme = theme,
                height = keyHeight,
                isSpecialKey = true,
                isActive = shiftState != ShiftState.OFF,
                modifier = Modifier.weight(1.4f),
                onPress = { onShiftToggle() }
            )

            row3.forEach { key ->
                val text = if (shiftState != ShiftState.OFF) key.uppercase() else key
                KeyButton(
                    label = text,
                    theme = theme,
                    height = keyHeight,
                    modifier = Modifier.weight(1f),
                    onPress = { pos -> onKeyPress(text, pos) }
                )
            }

            KeyButton(
                icon = Icons.AutoMirrored.Filled.Backspace,
                theme = theme,
                height = keyHeight,
                isSpecialKey = true,
                modifier = Modifier.weight(1.4f),
                onPress = { pos -> onBackspace(pos) }
            )
        }

        // Row 4 (123, Emoji, Space, Period, Enter)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            KeyButton(
                label = "?123",
                theme = theme,
                height = keyHeight,
                isSpecialKey = true,
                modifier = Modifier.weight(1.3f),
                onPress = { onModeToggle(KeyboardMode.NUMBERS) }
            )

            KeyButton(
                icon = Icons.Default.EmojiEmotions,
                theme = theme,
                height = keyHeight,
                isSpecialKey = true,
                modifier = Modifier.weight(1f),
                onPress = { onModeToggle(KeyboardMode.EMOJI) }
            )

            KeyButton(
                label = "Glass Keyboard",
                theme = theme,
                height = keyHeight,
                modifier = Modifier.weight(3.8f),
                onPress = { pos -> onSpace(pos) }
            )

            KeyButton(
                label = ".",
                theme = theme,
                height = keyHeight,
                modifier = Modifier.weight(1f),
                onPress = { pos -> onKeyPress(".", pos) }
            )

            KeyButton(
                icon = Icons.AutoMirrored.Filled.KeyboardReturn,
                theme = theme,
                height = keyHeight,
                isSpecialKey = true,
                isActive = true,
                modifier = Modifier.weight(1.3f),
                onPress = { pos -> onEnter(pos) }
            )
        }
    }
}

@Composable
fun SymbolsNumbersLayout(
    isSymbols: Boolean,
    theme: KeyboardTheme,
    onKeyPress: (String, Offset) -> Unit,
    onBackspace: (Offset) -> Unit,
    onEnter: (Offset) -> Unit,
    onSpace: (Offset) -> Unit,
    onModeToggle: (KeyboardMode) -> Unit
) {
    val keyHeight = (44 * theme.keyHeightScale).dp

    val numbersRow1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val numbersRow2 = listOf("@", "#", "$", "_", "&", "-", "+", "(", ")", "/")
    val numbersRow3 = listOf("*", "\"", "'", ":", ";", "!", "?")

    val symbolsRow1 = listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆")
    val symbolsRow2 = listOf("£", "¥", "€", "¢", "^", "°", "=", "{", "}", "\\")
    val symbolsRow3 = listOf("%", "©", "®", "™", "✓", "[", "]")

    val current1 = if (isSymbols) symbolsRow1 else numbersRow1
    val current2 = if (isSymbols) symbolsRow2 else numbersRow2
    val current3 = if (isSymbols) symbolsRow3 else numbersRow3

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            current1.forEach { symbol ->
                KeyButton(
                    label = symbol,
                    theme = theme,
                    height = keyHeight,
                    modifier = Modifier.weight(1f),
                    onPress = { pos -> onKeyPress(symbol, pos) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            current2.forEach { symbol ->
                KeyButton(
                    label = symbol,
                    theme = theme,
                    height = keyHeight,
                    modifier = Modifier.weight(1f),
                    onPress = { pos -> onKeyPress(symbol, pos) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            KeyButton(
                label = if (isSymbols) "123" else "=\\<",
                theme = theme,
                height = keyHeight,
                isSpecialKey = true,
                modifier = Modifier.weight(1.4f),
                onPress = { onModeToggle(if (isSymbols) KeyboardMode.NUMBERS else KeyboardMode.SYMBOLS) }
            )

            current3.forEach { symbol ->
                KeyButton(
                    label = symbol,
                    theme = theme,
                    height = keyHeight,
                    modifier = Modifier.weight(1f),
                    onPress = { pos -> onKeyPress(symbol, pos) }
                )
            }

            KeyButton(
                icon = Icons.AutoMirrored.Filled.Backspace,
                theme = theme,
                height = keyHeight,
                isSpecialKey = true,
                modifier = Modifier.weight(1.4f),
                onPress = { pos -> onBackspace(pos) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            KeyButton(
                label = "ABC",
                theme = theme,
                height = keyHeight,
                isSpecialKey = true,
                modifier = Modifier.weight(1.3f),
                onPress = { onModeToggle(KeyboardMode.ALPHA) }
            )

            KeyButton(
                label = "space",
                theme = theme,
                height = keyHeight,
                modifier = Modifier.weight(4.8f),
                onPress = { pos -> onSpace(pos) }
            )

            KeyButton(
                icon = Icons.AutoMirrored.Filled.KeyboardReturn,
                theme = theme,
                height = keyHeight,
                isSpecialKey = true,
                isActive = true,
                modifier = Modifier.weight(1.3f),
                onPress = { pos -> onEnter(pos) }
            )
        }
    }
}

@Composable
fun EmojiKaomojiLayout(
    theme: KeyboardTheme,
    onKeyPress: (String, Offset) -> Unit,
    onBackspace: (Offset) -> Unit,
    onModeToggle: (KeyboardMode) -> Unit
) {
    val keyHeight = (42 * theme.keyHeightScale).dp

    val emojis = listOf(
        "😂", "😍", "🥰", "🔥", "✨", "🚀", "❤️", "👍", "🙏", "🎉",
        "😎", "🥳", "💯", "👏", "⚡", "😴", "🤔", "👀", "🙌", "💀",
        "😭", "🥺", "💪", "💡", "🎯", "⭐", "🌟", "💖", "🌸", "☕"
    )

    val kaomojis = listOf(
        "(◕‿◕✿)", "(╯°□°)╯︵ ┻━┻", "¯\\_(ツ)_/¯", "(•_•)",
        "(─‿─)", "(≧◡≦)", "(͡° ͜ʖ ͡°)", "(>_<)"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Emojis grid
        val chunks = emojis.chunked(10)
        chunks.forEach { rowEmojis ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                rowEmojis.forEach { emoji ->
                    KeyButton(
                        label = emoji,
                        theme = theme,
                        height = keyHeight,
                        modifier = Modifier.weight(1f),
                        onPress = { pos -> onKeyPress(emoji, pos) }
                    )
                }
            }
        }

        // Kaomojis strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            kaomojis.forEach { kaomoji ->
                KeyButton(
                    label = kaomoji,
                    theme = theme,
                    height = keyHeight,
                    modifier = Modifier.padding(horizontal = 2.dp),
                    onPress = { pos -> onKeyPress(kaomoji, pos) }
                )
            }
        }

        // Bottom Nav Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            KeyButton(
                label = "ABC",
                theme = theme,
                height = keyHeight,
                isSpecialKey = true,
                modifier = Modifier.weight(1.5f),
                onPress = { onModeToggle(KeyboardMode.ALPHA) }
            )

            KeyButton(
                icon = Icons.AutoMirrored.Filled.Backspace,
                theme = theme,
                height = keyHeight,
                isSpecialKey = true,
                modifier = Modifier.weight(1.5f),
                onPress = { pos -> onBackspace(pos) }
            )
        }
    }
}

@Composable
fun QuickPhrasesLayout(
    phrases: List<QuickPhrase>,
    theme: KeyboardTheme,
    onPhraseSelect: (QuickPhrase) -> Unit,
    onModeToggle: (KeyboardMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(
            text = "⚡ Quick Snippets & Signatures",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        val accentColor = try {
            Color(android.graphics.Color.parseColor(theme.accentColorHex))
        } catch (_: Exception) {
            Color(0xFF818CF8)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            phrases.forEach { phrase ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.35f))
                        .clickable { onPhraseSelect(phrase) }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Column {
                        Text(
                            text = phrase.title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = phrase.text,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            KeyButton(
                label = "Back to Keyboard",
                theme = theme,
                height = 42.dp,
                isSpecialKey = true,
                modifier = Modifier.fillMaxWidth(),
                onPress = { onModeToggle(KeyboardMode.ALPHA) }
            )
        }
    }
}
