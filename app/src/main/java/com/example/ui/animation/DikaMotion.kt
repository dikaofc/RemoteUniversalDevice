package com.example.ui.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Dika Remote Motion System.
 * High performance, interruptible, GPU-accelerated motion modifiers.
 */
object DikaMotion {
    val ShortDuration = AnimationTokens.DurationFast
    val MediumDuration = AnimationTokens.DurationNormal
    val LongDuration = AnimationTokens.DurationSlow

    val DefaultPressScale = AnimationTokens.PressScaleDefault
    val SoftPressScale = AnimationTokens.PressScaleSoft
    val HardPressScale = AnimationTokens.PressScaleHard

    val FastSpring = DikaSpring.Fast
    val NormalSpring = DikaSpring.Normal
    val SoftSpring = DikaSpring.Soft
    val SnappySpring = DikaSpring.Snappy
    val BouncySpring = DikaSpring.Bouncy
}

/**
 * Fluid, interruptible glass button press modifier.
 * Uses GPU graphicsLayer to eliminate recomposition overhead.
 * Instantly reacts to touch down and release with spring physics and haptics.
 */
fun Modifier.dikaPressable(
    enabled: Boolean = true,
    pressedScale: Float = DikaMotion.DefaultPressScale,
    pressedAlpha: Float = 0.92f,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
): Modifier = composed {
    if (!enabled) return@composed this

    val haptic = LocalHapticFeedback.current
    val quality = LocalAnimationQuality.current
    val coroutineScope = rememberCoroutineScope()

    val scaleAnim = remember { Animatable(1f) }
    val alphaAnim = remember { Animatable(1f) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            if (quality.isPhysicsEnabled) {
                coroutineScope.launch {
                    scaleAnim.animateTo(
                        targetValue = pressedScale,
                        animationSpec = DikaSpring.Snappy
                    )
                }
                coroutineScope.launch {
                    alphaAnim.animateTo(
                        targetValue = pressedAlpha,
                        animationSpec = DikaSpring.Fast
                    )
                }
            } else {
                scaleAnim.snapTo(pressedScale)
                alphaAnim.snapTo(pressedAlpha)
            }
        } else {
            if (quality.isPhysicsEnabled) {
                coroutineScope.launch {
                    scaleAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = DikaSpring.Snappy
                    )
                }
                coroutineScope.launch {
                    alphaAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = DikaSpring.Fast
                    )
                }
            } else {
                scaleAnim.snapTo(1f)
                alphaAnim.snapTo(1f)
            }
        }
    }

    this
        .graphicsLayer {
            scaleX = scaleAnim.value
            scaleY = scaleAnim.value
            alpha = alphaAnim.value
        }
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null, // No standard Android ripple grey box overlay
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    }
                )
            } else Modifier
        )
}

/**
 * Error shake effect for invalid user actions or command failures.
 */
fun Modifier.dikaErrorShake(trigger: Int): Modifier = composed {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger > 0) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 350
                    0f at 0
                    (-12f) at 50
                    12f at 100
                    (-8f) at 150
                    8f at 200
                    (-4f) at 250
                    0f at 300
                }
            )
        }
    }

    this.graphicsLayer {
        translationX = shakeOffset.value
    }
}

/**
 * Command success pulse animation for buttons.
 */
fun Modifier.dikaSuccessPulse(trigger: Int): Modifier = composed {
    val scaleAnim = remember { Animatable(1f) }

    LaunchedEffect(trigger) {
        if (trigger > 0) {
            scaleAnim.animateTo(
                targetValue = 1.08f,
                animationSpec = DikaSpring.Fast
            )
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = DikaSpring.Bouncy
            )
        }
    }

    this.graphicsLayer {
        scaleX = scaleAnim.value
        scaleY = scaleAnim.value
    }
}

/**
 * Connecting status pulse or discovery aura ring animation on GPU graphicsLayer.
 */
fun Modifier.dikaPulse(enabled: Boolean = true): Modifier = composed {
    if (!enabled) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        this.alpha = alpha
    }
}
