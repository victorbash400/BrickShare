package com.example.brickshare.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = HederaGreen,
    secondary = HederaGreen,
    tertiary = NavyGrey,
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
    secondary = DeepNavy,
    tertiary = NavyGrey,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = DeepNavy,
    onSecondary = BuildingBlocksWhite,
    onTertiary = DeepNavy,
    onBackground = DeepNavy,
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
            WindowCompat.setDecorFitsSystemWindows(window, false) // Edge-to-edge

            // For API < 35, set status bar color directly
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                window.statusBarColor = Color.Black.toArgb()
            }
            // Always set light icons (white) for black background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            // Draw a black background behind the status bar for API 35+
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black) // Black background for status bar area
            ) {
                // Content with padding to avoid overlapping status bar icons
                Box(modifier = Modifier.statusBarsPadding()) {
                    content()
                }
            }
        }
    )
}