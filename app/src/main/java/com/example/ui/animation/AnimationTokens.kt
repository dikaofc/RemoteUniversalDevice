package com.example.ui.animation

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centralized Animation Tokens for Dika Remote.
 * Keeps motion, duration, scale, and spring parameters consistent across the entire application.
 */
object AnimationTokens {
    // Scales
    const val PressScaleDefault = 0.95f
    const val PressScaleSoft = 0.97f
    const val PressScaleHard = 0.92f
    const val DeviceCardPressScale = 0.96f
    const val IconSelectedScale = 1.12f
    const val StarFavoriteScale = 1.25f

    // Alpha / Opacity
    const val PressedAlpha = 0.92f
    const val DisabledAlpha = 0.40f
    const val HighHighlightAlpha = 0.30f
    const val LowHighlightAlpha = 0.08f

    // Durations (in Milliseconds)
    const val DurationInstant = 100
    const val DurationFast = 180
    const val DurationNormal = 300
    const val DurationSlow = 450
    const val DurationAmbientGlow = 12000

    // Spring Physics Constants
    const val StiffnessSnappy = 1600f
    const val StiffnessFast = 1200f
    const val StiffnessMedium = 800f
    const val StiffnessSoft = 400f

    const val DampingSnappy = 0.88f
    const val DampingFast = 0.82f
    const val DampingBouncy = 0.68f

    // Offset & Elevation
    val ElevationIdle: Dp = 4.dp
    val ElevationPressed: Dp = 1.dp
    val ElevationPower: Dp = 8.dp
}
