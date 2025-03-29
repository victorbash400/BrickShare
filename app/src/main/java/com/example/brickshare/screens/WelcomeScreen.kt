package com.example.brickshare.screens

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brickshare.R
import com.example.brickshare.ui.theme.BrickShareFonts
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    navController: NavController,
    signInLauncher: ActivityResultLauncher<android.content.Intent>,
    googleSignInClient: GoogleSignInClient
) {
    val colorScheme = darkColorScheme(
        primary = Color(0xFF90CAF9),
        onPrimary = Color.Black,
        secondary = Color(0xFF00C4B4), // Hedera-inspired teal
        background = Color(0xFF0A0A0A),
        surface = Color(0xFF1A1A1A),
        onBackground = Color.White,
        onSurface = Color.White
    )

    var showSplash by remember { mutableStateOf(true) }
    val currentUser by produceState<FirebaseUser?>(initialValue = FirebaseAuth.getInstance().currentUser) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            value = auth.currentUser
        }
        FirebaseAuth.getInstance().addAuthStateListener(listener)
        awaitDispose { FirebaseAuth.getInstance().removeAuthStateListener(listener) }
    }

    LaunchedEffect(showSplash, currentUser) {
        if (!showSplash && currentUser != null) {
            navController.navigate("role_selection") {
                popUpTo("welcome") { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(2500) // Slightly longer for animation
        showSplash = false
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography(/* Same as before */)) {
        if (showSplash) {
            SplashScreen()
        } else {
            MainWelcomeScreen(signInLauncher, googleSignInClient)
        }
    }
}

@Composable
private fun SplashScreen() {
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0A0A), Color.Black)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_round),
                contentDescription = "BrickShare Logo",
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .shadow(16.dp, CircleShape),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Glowing "Powered by Hedera"
            Text(
                text = "Powered by Hedera",
                color = Color(0xFF00C4B4).copy(alpha = glowAlpha),
                fontFamily = BrickShareFonts.Halcyon,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF00C4B4).copy(alpha = 0.1f), Color.Transparent),
                            radius = 100f
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun MainWelcomeScreen(
    signInLauncher: ActivityResultLauncher<android.content.Intent>,
    googleSignInClient: GoogleSignInClient
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0A0A), Color.Black, Color(0xFF050505))
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x2000C4B4), Color.Transparent),
                        radius = 1400f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 140.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_round),
                    contentDescription = "BrickShare Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .shadow(12.dp, CircleShape),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "BrickShare",
                    color = Color.White,
                    fontFamily = BrickShareFonts.Halcyon,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 42.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Own • Invest • Prosper",
                    color = Color(0xFFD0D0D0),
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 18.sp,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp)
            ) {
                ElevatedButton(
                    onClick = { signInLauncher.launch(googleSignInClient.signInIntent) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google), // Add Google icon drawable
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Continue with Google",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = BrickShareFonts.Halcyon
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Sign in with your Google account",
                    color = Color(0xFFB0B0B0),
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}