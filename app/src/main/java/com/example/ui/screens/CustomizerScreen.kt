package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardTheme
import com.example.ui.keyboard.GlassKeyboardView

@Composable
fun CustomizerScreen(
    activeTheme: KeyboardTheme,
    allThemes: List<KeyboardTheme>,
    onSelectTheme: (KeyboardTheme) -> Unit,
    onUpdateTheme: (KeyboardTheme) -> Unit,
    onSaveTheme: (KeyboardTheme) -> Unit,
    onDeleteTheme: (KeyboardTheme) -> Unit
) {
    var themeNameInput by remember(activeTheme) { mutableStateOf(activeTheme.name) }

    val soundStyles = listOf("MECHANICAL", "BUBBLE", "SOFT_TAP", "CYBER_PULSE", "TYPEWRITER", "SILENT")
    val particleStyles = listOf("NEON_SPARKLES", "GLASS_SHARDS", "GLOW_ORBS", "NONE")

    val accentColor = remember(activeTheme) {
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
        Text(
            text = "🎨 Glass Theme Customizer Studio",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Preset Themes Carousel
        Text(
            text = "Select Preset Theme",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allThemes.forEach { theme ->
                val isSelected = theme.id == activeTheme.id
                val themeGlow = try {
                    Color(android.graphics.Color.parseColor(theme.borderGlowColorHex))
                } catch (_: Exception) {
                    Color(0xFF38BDF8)
                }

                Card(
                    modifier = Modifier
                        .width(130.dp)
                        .clickable {
                            onSelectTheme(theme)
                        }
                        .testTag("theme_preset_${theme.name}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) themeGlow.copy(alpha = 0.3f) else Color(0xFF1E293B)
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, themeGlow) else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(themeGlow),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = theme.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        if (!theme.isPreset) {
                            Text(
                                text = "Custom",
                                color = accentColor,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Customizer Controls Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.9f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Name Input
                OutlinedTextField(
                    value = themeNameInput,
                    onValueChange = {
                        themeNameInput = it
                        onUpdateTheme(activeTheme.copy(name = it))
                    },
                    label = { Text("Theme Name", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Glass Opacity Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Glass Translucency", color = Color.White, fontSize = 13.sp)
                        Text("${(activeTheme.glassOpacity * 100).toInt()}%", color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = activeTheme.glassOpacity,
                        onValueChange = { onUpdateTheme(activeTheme.copy(glassOpacity = it)) },
                        valueRange = 0.15f..0.85f,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )
                }

                // Key Corner Radius Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Key Corner Radius", color = Color.White, fontSize = 13.sp)
                        Text("${activeTheme.cornerRadiusDp.toInt()} dp", color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = activeTheme.cornerRadiusDp,
                        onValueChange = { onUpdateTheme(activeTheme.copy(cornerRadiusDp = it)) },
                        valueRange = 4f..24f,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )
                }

                // Glow Intensity
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Glow Border Intensity", color = Color.White, fontSize = 13.sp)
                        Text("${(activeTheme.glowIntensity * 100).toInt()}%", color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = activeTheme.glowIntensity,
                        onValueChange = { onUpdateTheme(activeTheme.copy(glowIntensity = it)) },
                        valueRange = 0.2f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )
                }

                // Audio Sound Style Picker
                Column {
                    Text("Keyboard Click Sound", color = Color.White, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        soundStyles.forEach { sound ->
                            val isSelected = activeTheme.soundStyle == sound
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) accentColor else Color.White.copy(alpha = 0.1f))
                                    .clickable { onUpdateTheme(activeTheme.copy(soundStyle = sound)) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = sound,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Particle Effect Picker
                Column {
                    Text("Key Press Particle Burst", color = Color.White, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        particleStyles.forEach { particle ->
                            val isSelected = activeTheme.particleEffect == particle
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) accentColor else Color.White.copy(alpha = 0.1f))
                                    .clickable { onUpdateTheme(activeTheme.copy(particleEffect = particle)) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = particle,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Haptic Feedback Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Haptic Vibration", color = Color.White, fontSize = 13.sp)
                    Switch(
                        checked = activeTheme.hapticEnabled,
                        onCheckedChange = { onUpdateTheme(activeTheme.copy(hapticEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = accentColor, checkedTrackColor = accentColor.copy(alpha = 0.5f))
                    )
                }

                // Action Buttons Row (Save / Delete)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val newTheme = activeTheme.copy(
                                id = 0, // Create new entity if modified
                                name = themeNameInput.ifBlank { "Custom Glass Theme" },
                                isPreset = false
                            )
                            onSaveTheme(newTheme)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Theme", fontWeight = FontWeight.Bold)
                    }

                    if (!activeTheme.isPreset) {
                        IconButton(
                            onClick = { onDeleteTheme(activeTheme) },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }

        // Live Interactive Keyboard Preview
        Text(
            text = "⚡ Live Customizer Preview",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        GlassKeyboardView(
            theme = activeTheme,
            onKeyPress = {},
            onBackspace = {},
            onEnter = {},
            onSpace = {}
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}
