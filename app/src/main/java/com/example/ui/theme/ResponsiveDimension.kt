package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSizeClass {
    COMPACT, MEDIUM, EXPANDED
}

@Composable
@ReadOnlyComposable
fun getWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    return when {
        screenWidth < 600 -> WindowSizeClass.COMPACT
        screenWidth < 840 -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }
}

object ResponsiveDimension {
    val buttonHeight: Dp
        @Composable
        @ReadOnlyComposable
        get() = when (getWindowSizeClass()) {
            WindowSizeClass.COMPACT -> 48.dp
            WindowSizeClass.MEDIUM -> 56.dp
            WindowSizeClass.EXPANDED -> 64.dp
        }

    val iconSize: Dp
        @Composable
        @ReadOnlyComposable
        get() = when (getWindowSizeClass()) {
            WindowSizeClass.COMPACT -> 24.dp
            WindowSizeClass.MEDIUM -> 28.dp
            WindowSizeClass.EXPANDED -> 32.dp
        }

    val horizontalPadding: Dp
        @Composable
        @ReadOnlyComposable
        get() = when (getWindowSizeClass()) {
            WindowSizeClass.COMPACT -> 16.dp
            WindowSizeClass.MEDIUM -> 24.dp
            WindowSizeClass.EXPANDED -> 32.dp
        }
        
    val cardPadding: Dp
        @Composable
        @ReadOnlyComposable
        get() = when (getWindowSizeClass()) {
            WindowSizeClass.COMPACT -> 16.dp
            WindowSizeClass.MEDIUM -> 20.dp
            WindowSizeClass.EXPANDED -> 24.dp
        }
}
