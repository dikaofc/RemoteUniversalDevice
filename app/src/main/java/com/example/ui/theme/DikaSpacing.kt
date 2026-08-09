package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centralized Spacing Design System for Dika Remote.
 * Eliminates magic numbers and ensures consistent, responsive padding and margins.
 */
object DikaSpacing {
    val None: Dp = 0.dp
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
    val huge: Dp = 48.dp

    // Button Standard Dimensions
    val TouchTargetMin: Dp = 48.dp
    val ButtonHeightSmall: Dp = 48.dp
    val ButtonHeightMedium: Dp = 52.dp
    val ButtonHeightLarge: Dp = 56.dp
    val MaxContentWidth: Dp = 720.dp

    @Composable
    fun responsivePadding(): Dp = DikaResponsiveSize.responsiveValue(
        min = 12.dp,
        max = 24.dp,
        compact = 16.dp,
        medium = 20.dp,
        expanded = 24.dp
    )

    @Composable
    fun responsiveGridSpacing(): Dp = DikaResponsiveSize.responsiveValue(
        min = 8.dp,
        max = 16.dp,
        compact = 8.dp,
        medium = 12.dp,
        expanded = 16.dp
    )
}
