package com.example.brickshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.brickshare.ui.theme.BrickShareTheme
import com.example.brickshare.navigation.Navigation
import com.example.brickshare.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrickShareTheme(darkTheme = true) { // Force dark theme for blue background
                Navigation(userViewModel)
            }
        }
    }
}