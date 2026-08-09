package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.animation.LocalAnimationQuality
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.GlassGradients

enum class AmbientGlowType {
    DEFAULT, TV, AC, AUDIO, POWER
}

@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    glowType: AmbientGlowType = AmbientGlowType.DEFAULT,
    content: @Composable () -> Unit
) {
    val quality = LocalAnimationQuality.current

    val animOffset = if (quality.isAmbientMotionAllowed) {
        val infiniteTransition = rememberInfiniteTransition(label = "ambient_blob")
        val offset by infiniteTransition.animateFloat(
            initialValue = -40f,
            targetValue = 40f,
            animationSpec = infiniteRepeatable(
                animation = tween(12000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "blob_offset"
        )
        offset
    } else {
        0f
    }

    val isAmoled = MaterialTheme.colorScheme.background == AmoledBackground
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val bgColor = MaterialTheme.colorScheme.background

    val glowColors = when (glowType) {
        AmbientGlowType.TV -> listOf(Color(0xFF38BDF8).copy(alpha = if (isAmoled) 0.15f else 0.22f), Color(0xFF818CF8).copy(alpha = 0.10f), Color.Transparent)
        AmbientGlowType.AC -> listOf(Color(0xFF06B6D4).copy(alpha = if (isAmoled) 0.15f else 0.22f), Color(0xFF0284C7).copy(alpha = 0.10f), Color.Transparent)
        AmbientGlowType.AUDIO -> listOf(Color(0xFFF97316).copy(alpha = if (isAmoled) 0.12f else 0.18f), Color(0xFFEAB308).copy(alpha = 0.08f), Color.Transparent)
        AmbientGlowType.POWER -> listOf(Color(0xFFEF4444).copy(alpha = if (isAmoled) 0.18f else 0.25f), Color(0xFF991B1B).copy(alpha = 0.10f), Color.Transparent)
        AmbientGlowType.DEFAULT -> listOf(primaryColor.copy(alpha = if (isAmoled) 0.12f else 0.18f), secondaryColor.copy(alpha = 0.08f), Color.Transparent)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Draw dynamic ambient lighting blobs behind content on DrawScope / GPU
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Top-Right Ambient Blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = glowColors,
                    center = Offset(width * 0.85f + animOffset, height * 0.15f - animOffset),
                    radius = width * 0.75f
                ),
                radius = width * 0.75f,
                center = Offset(width * 0.85f + animOffset, height * 0.15f - animOffset)
            )

            // Bottom-Left Ambient Blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        secondaryColor.copy(alpha = if (isAmoled) 0.08f else 0.12f),
                        primaryColor.copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.15f - animOffset, height * 0.85f + animOffset),
                    radius = width * 0.7f
                ),
                radius = width * 0.7f,
                center = Offset(width * 0.15f - animOffset, height * 0.85f + animOffset)
            )
        }

        content()
    }
}
