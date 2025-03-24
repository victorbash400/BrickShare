package com.example.brickshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.brickshare.ui.theme.BrickShareTheme
import com.example.brickshare.navigation.Navigation
import com.example.brickshare.viewmodel.UserViewModel
import android.graphics.Color
import androidx.core.view.WindowCompat // For insets controller

class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.statusBarColor = Color.TRANSPARENT // Transparent status bar
        window.setBackgroundDrawable(null) // Clear window background
        // Ensure status bar icons adjust to background (light icons in dark mode, dark in light)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !resources.configuration.isNightModeActive
        setContent {
            BrickShareTheme {
                Navigation(userViewModel)
            }
        }
    }
}