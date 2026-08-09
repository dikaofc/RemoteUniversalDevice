package com.example.ui.animation

import androidx.compose.runtime.*

/**
 * Animation Quality settings for Dika Remote.
 * Allows adaptive performance scaling based on device refresh rate and hardware capabilities.
 */
enum class AnimationQualityLevel {
    ULTRA,      // Full 120Hz physics, glass blur, animated reflections, ambient particles
    HIGH,       // Standard 60-120Hz physics, glass blur, subtle reflection
    BALANCED,   // Lightweight spring physics, reduced blur, no continuous reflection
    LOW,        // Minimal animations, static glass opacity, optimal for battery/low-end
    AUTO        // Dynamically adjusted based on frame timing / dropped frames
}

data class AnimationQualityConfig(
    val level: AnimationQualityLevel = AnimationQualityLevel.HIGH,
    val isReducedMotionEnabled: Boolean = false,
    val isStartupComplete: Boolean = true
) {
    val isBlurAllowed: Boolean
        get() = isStartupComplete && !isReducedMotionEnabled && (level == AnimationQualityLevel.ULTRA || level == AnimationQualityLevel.HIGH)

    val isAmbientMotionAllowed: Boolean
        get() = isStartupComplete && !isReducedMotionEnabled && (level == AnimationQualityLevel.ULTRA || level == AnimationQualityLevel.HIGH)

    val isPhysicsEnabled: Boolean
        get() = level != AnimationQualityLevel.LOW
}

val LocalAnimationQuality = staticCompositionLocalOf { AnimationQualityConfig() }

/**
 * Remembers an adaptive AnimationQualityConfig that monitors real-time frame drops
 * and automatically adjusts blur and ambient motion intensity to preserve 60/90/120Hz fluid motion.
 */
@Composable
fun rememberAdaptiveAnimationQualityConfig(
    requestedLevel: AnimationQualityLevel = AnimationQualityLevel.AUTO,
    isReducedMotionEnabled: Boolean = false
): AnimationQualityConfig {
    if (requestedLevel != AnimationQualityLevel.AUTO) {
        return AnimationQualityConfig(level = requestedLevel, isReducedMotionEnabled = isReducedMotionEnabled)
    }

    val metrics by JankMonitor.metrics.collectAsState()

    val effectiveLevel = remember(metrics.fps, metrics.isJanky) {
        when {
            metrics.fps >= 90 -> AnimationQualityLevel.ULTRA
            metrics.fps >= 55 && !metrics.isJanky -> AnimationQualityLevel.HIGH
            metrics.fps >= 40 -> AnimationQualityLevel.BALANCED
            else -> AnimationQualityLevel.LOW
        }
    }

    return AnimationQualityConfig(
        level = effectiveLevel,
        isReducedMotionEnabled = isReducedMotionEnabled
    )
}

@Composable
fun DikaAnimationQualityProvider(
    config: AnimationQualityConfig,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalAnimationQuality provides config) {
        content()
    }
}
