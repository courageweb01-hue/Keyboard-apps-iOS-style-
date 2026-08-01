package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardTheme
import com.example.data.QuickPhrase
import com.example.ui.keyboard.GlassKeyboardView
import kotlinx.coroutines.delay

@Composable
fun PlaygroundScreen(
    activeTheme: KeyboardTheme,
    quickPhrases: List<QuickPhrase>,
    onLogSession: (wpm: Int, charCount: Int, durationSecs: Int, topKey: String) -> Unit
) {
    var typedText by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf<Long?>(null) }
    var currentWpm by remember { mutableStateOf(0) }
    var sessionDurationSeconds by remember { mutableStateOf(0) }
    val clipboardManager = LocalClipboardManager.current

    val samplePrompts = listOf(
        "Experience the future of typing with Glass Keyboard! ⚡",
        "The quick brown fox jumps over the lazy dog.",
        "Frosted glass acrylic keys with neon glowing edges.",
        "Quick phrases: On my way! 🏃‍♂️💨 Thank you so much! 🙏"
    )

    var currentPromptIndex by remember { mutableStateOf(0) }

    // WPM Calculator Timer
    LaunchedEffect(typedText) {
        if (typedText.isNotEmpty()) {
            if (startTime == null) {
                startTime = System.currentTimeMillis()
            }
            val now = System.currentTimeMillis()
            val elapsedMinutes = (now - (startTime ?: now)).toFloat() / 60000f
            sessionDurationSeconds = ((now - (startTime ?: now)) / 1000).toInt()
            if (elapsedMinutes > 0.05f) {
                val words = typedText.trim().split("\\s+".toRegex()).size
                currentWpm = (words / elapsedMinutes).toInt().coerceIn(0, 200)
            }
        }
    }

    val themeAccent = remember(activeTheme) {
        try {
            Color(android.graphics.Color.parseColor(activeTheme.accentColorHex))
        } catch (_: Exception) {
            Color(0xFF818CF8)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header & Speedometer Gauge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E293B).copy(alpha = 0.8f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Interactive Playground",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Theme: ${activeTheme.name}",
                            color = themeAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // WPM Pill Display
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(themeAccent, themeAccent.copy(alpha = 0.6f))
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "WPM",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$currentWpm WPM",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Realtime Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatPill("Characters", "${typedText.length}", themeAccent)
                    StatPill("Words", "${typedText.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size}", themeAccent)
                    StatPill("Time", "${sessionDurationSeconds}s", themeAccent)
                }
            }
        }

        // Target Practice Prompt Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F172A).copy(alpha = 0.7f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎯 Sample Practice Prompt",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            currentPromptIndex = (currentPromptIndex + 1) % samplePrompts.size
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Next Prompt",
                            tint = themeAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = samplePrompts[currentPromptIndex],
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Text Field Input Canvas
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F172A)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, themeAccent.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Typed Output",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )

                    Row {
                        IconButton(
                            onClick = {
                                if (typedText.isNotEmpty()) {
                                    clipboardManager.setText(AnnotatedString(typedText))
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                if (typedText.isNotEmpty()) {
                                    onLogSession(currentWpm, typedText.length, sessionDurationSeconds, "e")
                                }
                                typedText = ""
                                startTime = null
                                currentWpm = 0
                                sessionDurationSeconds = 0
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (typedText.isEmpty()) {
                        Text(
                            text = "Tap keys on the Glass Keyboard below to test typing...",
                            color = Color.White.copy(alpha = 0.35f),
                            fontSize = 15.sp
                        )
                    } else {
                        Text(
                            text = typedText,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Live In-App Embedded Glass Keyboard Preview
        Text(
            text = "⚡ Live Glass Keyboard Preview",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        GlassKeyboardView(
            theme = activeTheme,
            quickPhrases = quickPhrases,
            onKeyPress = { key ->
                typedText += key
            },
            onBackspace = {
                if (typedText.isNotEmpty()) {
                    typedText = typedText.dropLast(1)
                }
            },
            onEnter = {
                typedText += "\n"
            },
            onSpace = {
                typedText += " "
            },
            onPhraseSelect = { phrase ->
                // Quick phrase insertion handled by onKeyPress
            }
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun StatPill(label: String, value: String, accentColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp
        )
    }
}
