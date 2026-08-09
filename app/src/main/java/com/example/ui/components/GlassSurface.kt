package com.example.ui.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.GlassGradients
import com.example.ui.theme.LocalGlassTokens

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    blurRadius: Dp? = null,
    alpha: Float? = null,
    tint: Color? = null,
    borderStroke: BorderStroke? = null,
    elevation: Dp = 0.dp,
    showReflection: Boolean = true,
    content: @Composable () -> Unit
) {
    val tokens = LocalGlassTokens.current
    val effectiveBlur = blurRadius ?: tokens.blurRadius
    val effectiveAlpha = alpha ?: tokens.primaryAlpha
    val isDark = isSystemInDarkTheme() || MaterialTheme.colorScheme.background == AmoledBackground

    val surfaceTint = tint ?: if (isDark) {
        Color(0xFF1E293B).copy(alpha = effectiveAlpha)
    } else {
        Color.White.copy(alpha = effectiveAlpha.coerceAtLeast(0.5f))
    }

    val density = LocalDensity.current
    val blurPx = with(density) { effectiveBlur.toPx() }

    val defaultBorder = BorderStroke(
        width = tokens.borderStrokeWidth,
        brush = if (isDark) GlassGradients.highlightBorderDark else GlassGradients.highlightBorderLight
    )
    val effectiveBorder = borderStroke ?: defaultBorder

    // Version-aware graphics modifier for background blur layer on Android S+
    val blurModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && tokens.isEnabled && blurPx > 0f) {
        Modifier.graphicsLayer {
            renderEffect = RenderEffect.createBlurEffect(
                blurPx,
                blurPx,
                Shader.TileMode.CLAMP
            ).asComposeRenderEffect()
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(if (elevation > 0.dp) Modifier.shadow(elevation = elevation, shape = shape) else Modifier)
            .clip(shape)
    ) {
        // Background Glass Layer (Translucent tint, native blur, specular highlight, border)
        // Rendered in a separate background box so foreground content is NEVER blurred
        Box(
            modifier = Modifier
                .matchParentSize()
                .then(blurModifier)
                .background(surfaceTint)
                .then(
                    if (showReflection) {
                        Modifier.drawBehind {
                            // Subtle top-left glass specular reflection highlight
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = if (isDark) 0.12f else 0.30f),
                                        Color.White.copy(alpha = 0.02f),
                                        Color.Transparent
                                    ),
                                    start = Offset.Zero,
                                    end = Offset(size.width * 0.7f, size.height * 0.7f)
                                )
                            )
                        }
                    } else Modifier
                )
                .border(effectiveBorder, shape)
        )

        // Foreground Content Layer - rendered crisp, sharp, unblurred, and clear on top
        content()
    }
}
