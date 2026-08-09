package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

/**
 * Responsive Sizing Engine for Dika Remote.
 * Calculates dimensions dynamically based on available screen real estate.
 */
object DikaResponsiveSize {

    @Composable
    fun responsiveValue(
        min: Dp,
        max: Dp,
        compact: Dp = min,
        medium: Dp = (min + max) / 2,
        expanded: Dp = max
    ): Dp {
        val configuration = LocalConfiguration.current
        val width = configuration.screenWidthDp.dp
        
        return when {
            width < 600.dp -> compact
            width < 840.dp -> medium
            else -> expanded
        }.coerceIn(min, max)
    }

    /**
     * Calculates optimal button size for a grid based on available width.
     */
    fun calculateButtonSize(
        availableWidth: Dp,
        columns: Int,
        spacing: Dp,
        minSize: Dp = 48.dp,
        maxSize: Dp = 72.dp
    ): Dp {
        val totalSpacing = spacing * (columns - 1)
        val calculated = (availableWidth - totalSpacing) / columns
        return calculated.coerceIn(minSize, maxSize)
    }
}
