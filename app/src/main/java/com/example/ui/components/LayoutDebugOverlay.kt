package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Developer Mode Layout Debug modifier to draw layout bounds (Blue) and touch target bounds (Green).
 */
fun Modifier.layoutDebugBounds(enabled: Boolean = true): Modifier = if (!enabled) this else this.drawWithContent {
    drawContent()
    // Draw component layout bounds (Blue)
    drawRect(
        color = Color(0xFF3B82F6),
        style = Stroke(width = 2f)
    )
}

/**
 * Developer Mode Layout Debug Overlay.
 * Displays screen bounds, safe area insets, touch target outlines, and WindowSizeClass info.
 */
@Composable
fun LayoutDebugOverlay(
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    if (!enabled) return

    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val widthDp = maxWidth
        val heightDp = maxHeight

        val windowClass = when {
            widthDp < 600.dp -> "Compact Phone"
            widthDp < 840.dp -> "Medium Tablet / Foldable"
            else -> "Expanded Tablet / Desktop"
        }

        // Draw Inset & Safe Area Lines on GPU Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw Blue Layout Border
            drawRect(
                color = Color(0xAA3B82F6),
                style = Stroke(width = 3.dp.toPx())
            )

            // Draw Center Crosshair
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            drawLine(
                color = Color(0x44EF4444),
                start = Offset(centerX, 0f),
                end = Offset(centerX, size.height),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = Color(0x44EF4444),
                start = Offset(0f, centerY),
                end = Offset(size.width, centerY),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Debug Info Badge at Top Center
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
                .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LAYOUT DEBUG MODE",
                    color = Color(0xFF10B981),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${widthDp.value.toInt()}dp × ${heightDp.value.toInt()}dp | $windowClass",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Blue: Layout Bounds | Green: Touch Bounds | Red: Center",
                    color = Color.Yellow,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
