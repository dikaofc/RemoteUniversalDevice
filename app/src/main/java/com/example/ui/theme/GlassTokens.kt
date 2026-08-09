package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.GlassEffectsMode

data class GlassTokens(
    val blurRadius: Dp = 16.dp,
    val borderStrokeWidth: Dp = 1.dp,
    val primaryAlpha: Float = 0.25f,
    val secondaryAlpha: Float = 0.15f,
    val floatingAlpha: Float = 0.35f,
    val buttonAlpha: Float = 0.22f,
    val navAlpha: Float = 0.40f,
    val modalAlpha: Float = 0.50f,
    val highlightAlpha: Float = 0.30f,
    val shadowElevation: Dp = 8.dp,
    val isEnabled: Boolean = true
) {
    companion object {
        fun fromMode(mode: GlassEffectsMode): GlassTokens {
            return when (mode) {
                GlassEffectsMode.FULL -> GlassTokens(
                    blurRadius = 20.dp,
                    primaryAlpha = 0.22f,
                    secondaryAlpha = 0.12f,
                    floatingAlpha = 0.32f,
                    buttonAlpha = 0.25f,
                    navAlpha = 0.42f,
                    modalAlpha = 0.55f,
                    highlightAlpha = 0.35f,
                    isEnabled = true
                )
                GlassEffectsMode.BALANCED -> GlassTokens(
                    blurRadius = 10.dp,
                    primaryAlpha = 0.30f,
                    secondaryAlpha = 0.18f,
                    floatingAlpha = 0.40f,
                    buttonAlpha = 0.30f,
                    navAlpha = 0.50f,
                    modalAlpha = 0.65f,
                    highlightAlpha = 0.25f,
                    isEnabled = true
                )
                GlassEffectsMode.REDUCED -> GlassTokens(
                    blurRadius = 0.dp,
                    primaryAlpha = 0.45f,
                    secondaryAlpha = 0.28f,
                    floatingAlpha = 0.55f,
                    buttonAlpha = 0.40f,
                    navAlpha = 0.65f,
                    modalAlpha = 0.75f,
                    highlightAlpha = 0.20f,
                    isEnabled = false
                )
                GlassEffectsMode.OFF -> GlassTokens(
                    blurRadius = 0.dp,
                    primaryAlpha = 0.85f,
                    secondaryAlpha = 0.70f,
                    floatingAlpha = 0.90f,
                    buttonAlpha = 0.80f,
                    navAlpha = 0.92f,
                    modalAlpha = 0.95f,
                    highlightAlpha = 0.10f,
                    isEnabled = false
                )
            }
        }
    }
}

val LocalGlassTokens = staticCompositionLocalOf { GlassTokens() }

object GlassGradients {
    val highlightBorderLight = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 0.1f),
            Color.White.copy(alpha = 0.05f),
            Color.White.copy(alpha = 0.25f)
        )
    )

    val highlightBorderDark = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.35f),
            Color.White.copy(alpha = 0.08f),
            Color.White.copy(alpha = 0.02f),
            Color.White.copy(alpha = 0.20f)
        )
    )

    val reflectionOverlay = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.15f),
            Color.White.copy(alpha = 0.03f),
            Color.Transparent
        )
    )

    val powerButtonGlow = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFF4D4D).copy(alpha = 0.4f),
            Color(0xFFEF4444).copy(alpha = 0.15f),
            Color.Transparent
        )
    )

    val ambientTvGlow = Brush.radialGradient(
        colors = listOf(
            Color(0xFF38BDF8).copy(alpha = 0.18f),
            Color(0xFF818CF8).copy(alpha = 0.10f),
            Color.Transparent
        )
    )

    val ambientAcGlow = Brush.radialGradient(
        colors = listOf(
            Color(0xFF06B6D4).copy(alpha = 0.20f),
            Color(0xFF0284C7).copy(alpha = 0.10f),
            Color.Transparent
        )
    )
}
