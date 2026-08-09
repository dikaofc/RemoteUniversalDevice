package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.CompositionLocalProvider
import com.example.data.GlassEffectsMode
import com.example.data.ThemeMode
import com.example.ui.animation.DikaAnimationQualityProvider
import com.example.ui.animation.rememberAdaptiveAnimationQualityConfig

private val DarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    secondary = IndigoSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFF94A3B8)
)

private val AmoledColorScheme = darkColorScheme(
    primary = CyanPrimary,
    secondary = IndigoSecondary,
    background = AmoledBackground,
    surface = AmoledSurface,
    surfaceVariant = AmoledSurfaceVariant,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFA3A3A3)
)

private val LightColorScheme = lightColorScheme(
    primary = CyanPrimaryDark,
    secondary = IndigoSecondary,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF64748B)
)

@Composable
fun DikaRemoteTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    glassEffectsMode: GlassEffectsMode = GlassEffectsMode.FULL,
    isStartupComplete: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkSystem = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.AMOLED -> AmoledColorScheme
        ThemeMode.SYSTEM -> if (darkSystem) DarkColorScheme else LightColorScheme
    }

    val glassTokens = GlassTokens.fromMode(glassEffectsMode)
    val animationConfig = rememberAdaptiveAnimationQualityConfig().copy(isStartupComplete = isStartupComplete)

    CompositionLocalProvider(LocalGlassTokens provides glassTokens) {
        DikaAnimationQualityProvider(config = animationConfig) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography,
                content = content
            )
        }
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    DikaRemoteTheme(themeMode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT, content = content)
}

