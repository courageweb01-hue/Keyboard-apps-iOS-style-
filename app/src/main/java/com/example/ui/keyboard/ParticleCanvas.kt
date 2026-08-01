package com.example.ui.keyboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.random.Random

data class Particle(
    val startX: Float,
    val startY: Float,
    val angle: Float,
    val speed: Float,
    val radius: Float,
    val color: Color
)

@Composable
fun ParticleBurst(
    position: Offset,
    color: Color,
    particleType: String,
    onComplete: () -> Unit
) {
    if (particleType == "NONE") {
        LaunchedEffect(Unit) { onComplete() }
        return
    }

    val progress = remember { Animatable(0f) }

    val particles = remember(position) {
        val count = if (particleType == "GLASS_SHARDS") 12 else 8
        List(count) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 80f + 40f
            val radius = Random.nextFloat() * 6f + 3f
            Particle(
                startX = position.x,
                startY = position.y,
                angle = angle,
                speed = speed,
                radius = radius,
                color = color
            )
        }
    }

    LaunchedEffect(position) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )
        onComplete()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val p = progress.value
        val alpha = (1f - p).coerceIn(0f, 1f)

        particles.forEach { particle ->
            val distance = particle.speed * p
            val x = particle.startX + (Math.cos(particle.angle.toDouble()) * distance).toFloat()
            val y = particle.startY + (Math.sin(particle.angle.toDouble()) * distance).toFloat() - (p * 30f) // Float up

            drawParticle(particle, x, y, alpha, particleType)
        }
    }
}

private fun DrawScope.drawParticle(
    particle: Particle,
    x: Float,
    y: Float,
    alpha: Float,
    particleType: String
) {
    val drawColor = particle.color.copy(alpha = alpha)
    when (particleType) {
        "GLASS_SHARDS" -> {
            drawCircle(
                color = drawColor,
                radius = particle.radius * (1f - alpha * 0.3f),
                center = Offset(x, y)
            )
        }
        "GLOW_ORBS" -> {
            drawCircle(
                color = drawColor,
                radius = particle.radius * 1.5f,
                center = Offset(x, y)
            )
        }
        else -> { // NEON_SPARKLES
            drawCircle(
                color = drawColor,
                radius = particle.radius,
                center = Offset(x, y)
            )
        }
    }
}
