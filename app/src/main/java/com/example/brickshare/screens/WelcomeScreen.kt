package com.example.brickshare.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.brickshare.R
import com.example.brickshare.ui.theme.BrickShareFonts
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(navController: NavController) {
    val colorScheme = darkColorScheme(
        primary = Color(0xFF90CAF9),
        onPrimary = Color.Black,
        secondary = Color(0xFF26A69A),
        background = Color.Black,
        surface = Color(0xFF121212),
        onBackground = Color.White,
        onSurface = Color.White
    )

    // State for splash screen
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000) // Show splash for 2 seconds
        showSplash = false
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            displayLarge = MaterialTheme.typography.displayLarge.copy(fontFamily = BrickShareFonts.Halcyon),
            displayMedium = MaterialTheme.typography.displayMedium.copy(fontFamily = BrickShareFonts.Halcyon),
            displaySmall = MaterialTheme.typography.displaySmall.copy(fontFamily = BrickShareFonts.Halcyon),
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontFamily = BrickShareFonts.Halcyon),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontFamily = BrickShareFonts.Halcyon),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontFamily = BrickShareFonts.Halcyon),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = BrickShareFonts.Halcyon),
            titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = BrickShareFonts.Halcyon),
            titleSmall = MaterialTheme.typography.titleSmall.copy(fontFamily = BrickShareFonts.Halcyon),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = BrickShareFonts.Halcyon),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = BrickShareFonts.Halcyon),
            bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = BrickShareFonts.Halcyon),
            labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = BrickShareFonts.Halcyon),
            labelMedium = MaterialTheme.typography.labelMedium.copy(fontFamily = BrickShareFonts.Halcyon),
            labelSmall = MaterialTheme.typography.labelSmall.copy(fontFamily = BrickShareFonts.Halcyon)
        )
    ) {
        if (showSplash) {
            // Splash Screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_round),
                    contentDescription = "BrickShare Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(40.dp))
                )
            }
        } else {
            // Welcome Screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF121212), Color.Black, Color(0xFF050505))
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x10304FFE),
                                    Color(0x0526A69A)
                                ),
                                radius = 1200f
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 120.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(40.dp),
                            shadowElevation = 8.dp,
                            color = Color.Transparent
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_logo_round),
                                contentDescription = "BrickShare Logo",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(40.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "BrickShare",
                            color = Color.White,
                            fontFamily = BrickShareFonts.Halcyon,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            fontSize = 38.sp,
                            letterSpacing = 0.sp,
                            style = MaterialTheme.typography.displaySmall
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Own • Invest • Prosper",
                            color = Color(0xFFBBBBBB),
                            fontFamily = BrickShareFonts.Halcyon,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 64.dp)
                    ) {
                        ElevatedButton(
                            onClick = { navController.navigate("signin") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary
                            ),
                            elevation = ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 2.dp
                            )
                        ) {
                            Text(
                                "Sign In",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = BrickShareFonts.Halcyon
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        FilledTonalButton(
                            onClick = { navController.navigate("signup") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0x3090CAF9),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                "Create Account",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = BrickShareFonts.Halcyon
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        TextButton(
                            onClick = { /* Handle terms click */ },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFFAAAAAA)
                            )
                        ) {
                            Text(
                                text = "Privacy Policy and Terms",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWelcomeScreen() {
    WelcomeScreen(navController = rememberNavController())
}