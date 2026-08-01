package com.example.ui.keyboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardTheme

@Composable
fun KeyButton(
    label: String? = null,
    icon: ImageVector? = null,
    theme: KeyboardTheme,
    modifier: Modifier = Modifier,
    isSpecialKey: Boolean = false,
    isActive: Boolean = false,
    height: Dp = 48.dp,
    onPress: (Offset) -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }
    var keyCenterPosition by remember { mutableStateOf(Offset.Zero) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "keyScale"
    )

    // Parse colors from theme
    val glassBg = remember(theme) {
        val baseColor = try {
            Color(android.graphics.Color.parseColor(theme.keyBgColorHex))
        } catch (_: Exception) {
            Color.White.copy(alpha = 0.2f)
        }
        if (isActive) {
            try {
                Color(android.graphics.Color.parseColor(theme.accentColorHex))
            } catch (_: Exception) {
                Color(0xFF818CF8)
            }
        } else if (isSpecialKey) {
            baseColor.copy(alpha = (theme.glassOpacity * 1.3f).coerceAtMost(0.9f))
        } else {
            baseColor.copy(alpha = theme.glassOpacity)
        }
    }

    val glowBorderColor = remember(theme, isPressed) {
        try {
            val baseGlow = Color(android.graphics.Color.parseColor(theme.borderGlowColorHex))
            if (isPressed) baseGlow else baseGlow.copy(alpha = theme.glowIntensity * 0.6f)
        } catch (_: Exception) {
            Color(0xFF38BDF8).copy(alpha = 0.5f)
        }
    }

    val textColor = remember(theme, isActive) {
        if (isActive) Color.White
        else {
            try {
                Color(android.graphics.Color.parseColor(theme.keyTextColorHex))
            } catch (_: Exception) {
                Color.White
            }
        }
    }

    val shape = RoundedCornerShape(theme.cornerRadiusDp.dp)

    Box(
        modifier = modifier
            .height(height)
            .scale(scale)
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInRoot()
                keyCenterPosition = Offset(
                    bounds.left + bounds.width / 2,
                    bounds.top + bounds.height / 2
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        isPressed = true
                        onPress(keyCenterPosition)
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glassBg.copy(alpha = (glassBg.alpha * 1.2f).coerceAtMost(0.95f)),
                        glassBg.copy(alpha = (glassBg.alpha * 0.8f).coerceAtLeast(0.1f))
                    )
                )
            )
            .border(
                width = if (isPressed) 1.5.dp else 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        glowBorderColor,
                        glowBorderColor.copy(alpha = 0.15f)
                    )
                ),
                shape = shape
            )
            .testTag("key_${label ?: "icon"}"),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = label ?: "Key",
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
        } else if (label != null) {
            Text(
                text = label,
                color = textColor,
                fontSize = if (label.length > 2) 12.sp else 16.sp,
                fontWeight = if (isSpecialKey || isActive) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        // Key Press Popup Bubble
        if (isPressed && label != null && label.length == 1 && !isSpecialKey) {
            Box(
                modifier = Modifier
                    .offset(y = (-54).dp)
                    .size(46.dp, 50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(glowBorderColor.copy(alpha = 0.9f))
                    .border(1.dp, Color.White, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
