package com.example.ui.animation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring

/**
 * iOS-inspired Physics-Based Spring Presets for Dika Remote.
 * Designed for low latency, natural movement, and interruptible touch feedback.
 */
object DikaSpring {
    /**
     * Fast & responsive spring for immediate touch feedback, micro-interactions,
     * and high-frequency button presses.
     */
    val Fast: SpringSpec<Float> = spring(
        dampingRatio = 0.82f,
        stiffness = 1200f
    )

    /**
     * Balanced spring for standard UI state changes, sheet dismissals,
     * and component transitions.
     */
    val Normal: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /**
     * Soft spring with subtle deceleration for smooth floating background elements,
     * ambient cards, and gentle page transitions.
     */
    val Soft: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )

    /**
     * Light bouncy spring with minimal overshoot for delightful success indicators,
     * favorite icons, and floating action button pops.
     */
    val Bouncy: SpringSpec<Float> = spring(
        dampingRatio = 0.68f,
        stiffness = Spring.StiffnessMediumLow
    )

    /**
     * Ultra-snappy spring for hardware remote button presses, D-Pad navigation,
     * and immediate haptic-synced reactions.
     */
    val Snappy: SpringSpec<Float> = spring(
        dampingRatio = 0.88f,
        stiffness = 1600f
    )

    /**
     * Dp variant for size / offset animation specs.
     */
    fun <T> dpSpring(dampingRatio: Float = 0.85f, stiffness: Float = Spring.StiffnessMedium): SpringSpec<T> {
        return spring(dampingRatio = dampingRatio, stiffness = stiffness)
    }
}
