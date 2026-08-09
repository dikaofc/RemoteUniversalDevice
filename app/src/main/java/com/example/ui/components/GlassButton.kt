package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.animation.DikaMotion
import com.example.ui.animation.DikaSpring
import com.example.ui.animation.dikaErrorShake
import com.example.ui.animation.dikaSuccessPulse
import com.example.ui.common.HapticFeedbackHelper
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.GlassGradients
import com.example.ui.theme.LocalGlassTokens
import com.example.ui.theme.DikaSpacing
import com.example.ui.theme.DikaResponsiveSize
import com.example.ui.debug.DebugSizeOverlay

@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    containerColor: Color? = null,
    contentColor: Color? = null,
    isPowerButton: Boolean = false,
    errorTrigger: Int = 0,
    successTrigger: Int = 0,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = DikaSpring.Snappy,
        label = "button_scale"
    )

    val alphaVal by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = DikaSpring.Fast,
        label = "button_alpha"
    )

    val context = LocalContext.current
    val hapticHelper = remember(context) { HapticFeedbackHelper(context) }
    val tokens = LocalGlassTokens.current
    val isDark = isSystemInDarkTheme() || MaterialTheme.colorScheme.background == AmoledBackground
    val spacing = DikaSpacing

    val baseColor = containerColor ?: if (isPowerButton) {
        Color(0xFFEF4444).copy(alpha = if (isPressed) 0.85f else 0.35f)
    } else if (isDark) {
        Color.White.copy(alpha = if (isPressed) 0.35f else tokens.buttonAlpha)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = if (isPressed) 0.40f else 0.15f)
    }

    val borderStroke = BorderStroke(
        width = 1.dp,
        brush = if (isPowerButton) {
            Brush.linearGradient(listOf(Color(0xFFFFAAAA), Color(0xFFEF4444)))
        } else if (isDark) {
            GlassGradients.highlightBorderDark
        } else {
            GlassGradients.highlightBorderLight
        }
    )

    DebugSizeOverlay {
        Box(
            modifier = modifier
                .defaultMinSize(minWidth = spacing.TouchTargetMin, minHeight = spacing.TouchTargetMin)
                .dikaErrorShake(errorTrigger)
                .dikaSuccessPulse(successTrigger)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = if (enabled) alphaVal else 0.40f
                }
                .shadow(
                    elevation = if (isPowerButton) 10.dp else 4.dp,
                    shape = shape,
                    ambientColor = if (isPowerButton) Color(0xFFEF4444) else Color.Black,
                    spotColor = if (isPowerButton) Color(0xFFEF4444) else Color.Black
                )
                .clip(shape)
                .background(baseColor)
                .border(borderStroke, shape)
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    hapticHelper.performHaptic(isPowerButton = isPowerButton)
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }
        }
    }
}

@Composable
fun GlassRemoteButton(
    icon: ImageVector,
    label: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp? = null, // Adaptive if null
    isPowerButton: Boolean = false,
    accentColor: Color? = null,
    enabled: Boolean = true,
    errorTrigger: Int = 0,
    successTrigger: Int = 0
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1.0f,
        animationSpec = DikaSpring.Snappy,
        label = "remote_btn_scale"
    )

    val alphaVal by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = DikaSpring.Fast,
        label = "remote_btn_alpha"
    )

    val context = LocalContext.current
    val hapticHelper = remember(context) { HapticFeedbackHelper(context) }
    val isDark = isSystemInDarkTheme() || MaterialTheme.colorScheme.background == AmoledBackground
    
    val defaultButtonSize = DikaResponsiveSize.responsiveValue(
        min = 48.dp,
        max = 72.dp,
        compact = 56.dp,
        medium = 64.dp,
        expanded = 72.dp
    )
    
    val actualSize = size ?: defaultButtonSize

    val btnColor = if (isPowerButton) {
        Color(0xFFEF4444).copy(alpha = if (isPressed) 0.90f else 0.40f)
    } else if (accentColor != null) {
        accentColor.copy(alpha = if (isPressed) 0.45f else 0.22f)
    } else if (isDark) {
        Color(0xFF334155).copy(alpha = if (isPressed) 0.60f else 0.35f)
    } else {
        Color.White.copy(alpha = if (isPressed) 0.90f else 0.65f)
    }

    val iconTint = if (isPowerButton) {
        Color.White
    } else if (accentColor != null) {
        accentColor
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    DebugSizeOverlay {
        Box(
            modifier = modifier
                .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                .size(actualSize)
                .dikaErrorShake(errorTrigger)
                .dikaSuccessPulse(successTrigger)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = if (enabled) alphaVal else 0.40f
                }
                .shadow(
                    elevation = if (isPowerButton) 8.dp else 2.dp,
                    shape = CircleShape,
                    ambientColor = if (isPowerButton) Color(0xFFEF4444) else Color.Transparent,
                    spotColor = if (isPowerButton) Color(0xFFEF4444) else Color.Transparent
                )
                .clip(CircleShape)
                .background(btnColor)
                .border(
                    width = 1.dp,
                    brush = if (isPowerButton) {
                        Brush.linearGradient(listOf(Color(0xFFFF8888), Color(0xFFEF4444)))
                    } else if (isDark) {
                        GlassGradients.highlightBorderDark
                    } else {
                        GlassGradients.highlightBorderLight
                    },
                    shape = CircleShape
                )
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    hapticHelper.performHaptic(isPowerButton = isPowerButton)
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label ?: "Button",
                    tint = iconTint,
                    modifier = Modifier.size(if (actualSize >= 64.dp) 28.dp else 22.dp)
                )
                if (label != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = label,
                        fontSize = if (actualSize >= 64.dp) 10.sp else 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconTint,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

