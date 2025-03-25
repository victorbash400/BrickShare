package com.example.brickshare.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = HederaGreen,
    secondary = HederaGreen, // Using the same green for secondary actions
    tertiary = NavyGrey,     // Reintroduced NavyGrey for tertiary
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = BuildingBlocksWhite,
    onSecondary = BuildingBlocksWhite,
    onTertiary = BuildingBlocksWhite,
    onBackground = BuildingBlocksWhite,
    onSurface = BuildingBlocksWhite
)

private val LightColorScheme = lightColorScheme(
    primary = HederaGreen,
    secondary = DeepNavy,    // Using DeepNavy for secondary in light theme
    tertiary = NavyGrey,     // Using NavyGrey for tertiary
    background = LightBackground,
    surface = LightSurface,
    onPrimary = DeepNavy,    // DeepNavy for contrast on green in light theme
    onSecondary = BuildingBlocksWhite,
    onTertiary = DeepNavy,
    onBackground = DeepNavy, // DeepNavy for contrast on light background
    onSurface = DeepNavy
)

@Composable
fun BrickShareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Match status bar to background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assuming you have typography defined
        content = content
    )
}