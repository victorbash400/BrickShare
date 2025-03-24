package com.example.brickshare.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.brickshare.R
import com.example.brickshare.ui.theme.BrickShareFonts
import com.example.brickshare.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(navController: NavController, userViewModel: UserViewModel) {
    var selectedRole by remember { mutableStateOf("") }

    // Animation states
    val headerVisible = remember { MutableTransitionState(false).apply { targetState = true } }
    val cardsVisible = remember { MutableTransitionState(false).apply { targetState = true } }
    val buttonVisible = remember { MutableTransitionState(false).apply { targetState = true } }

    // Create color scheme to match welcome screen
    val colorScheme = darkColorScheme(
        primary = Color(0xFF90CAF9),
        onPrimary = Color.Black,
        secondary = Color(0xFF26A69A),
        background = Color.Black,
        surface = Color(0xFF121212),
        onBackground = Color.White,
        onSurface = Color.White
    )

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF121212), Color.Black, Color(0xFF050505))
                    )
                )
        ) {
            // Subtle pattern overlay
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                // Animated Header
                AnimatedVisibility(
                    visibleState = headerVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(
                        initialOffsetY = { -40 },
                        animationSpec = tween(500)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Title
                        Text(
                            text = "Select Your Role",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 28.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Subtitle
                        Text(
                            text = "How will you participate in BrickShare?",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 16.sp,
                            color = Color(0xFFBBBBBB),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Role options in a selectable group
                AnimatedVisibility(
                    visibleState = cardsVisible,
                    enter = fadeIn(tween(700)) + slideInVertically(
                        initialOffsetY = { 80 },
                        animationSpec = tween(700)
                    )
                ) {
                    Column(
                        modifier = Modifier.selectableGroup(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Investor Card
                        EnhancedRoleCard(
                            title = "Investor",
                            description = "Buy fractional shares of properties on Hedera and earn passive income",
                            isSelected = selectedRole == "investor",
                            onSelect = { selectedRole = "investor" },
                            colorScheme = colorScheme,
                            icon = Icons.Rounded.AccountBalance
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Property Owner Card
                        EnhancedRoleCard(
                            title = "Property Owner",
                            description = "Tokenize and list your properties for fractional ownership",
                            isSelected = selectedRole == "owner",
                            onSelect = { selectedRole = "owner" },
                            colorScheme = colorScheme,
                            icon = Icons.Rounded.Apartment
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Continue Button
                AnimatedVisibility(
                    visibleState = buttonVisible,
                    enter = fadeIn(tween(900)) + slideInVertically(
                        initialOffsetY = { 40 },
                        animationSpec = tween(900)
                    )
                ) {
                    ElevatedButton(
                        onClick = {
                            userViewModel.setUserRole(selectedRole)
                            navController.navigate("dashboard")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = selectedRole.isNotEmpty(),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary,
                            disabledContainerColor = Color(0xFF303030)
                        ),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 2.dp,
                            disabledElevation = 0.dp
                        )
                    ) {
                        Text(
                            "Continue",
                            fontSize = 18.sp,
                            fontFamily = BrickShareFonts.Halcyon
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedRoleCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    colorScheme: ColorScheme,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0x2090CAF9) else Color(0xFF1E1E1E),
            contentColor = if (isSelected) colorScheme.primary else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 1.dp
        ),
        border = if (isSelected) BorderStroke(2.dp, colorScheme.primary) else BorderStroke(1.dp, Color(0xFF333333))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with circular background
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color(0x3090CAF9) else Color(0xFF262626)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = if (isSelected) colorScheme.primary else Color(0xFFAAAAAA)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontFamily = BrickShareFonts.Halcyon,
                    color = if (isSelected) colorScheme.primary else Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    fontFamily = BrickShareFonts.Halcyon,
                    color = if (isSelected) Color(0xFFD0D0D0) else Color(0xFF9E9E9E)
                )
            }

            // Radio button
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = colorScheme.primary,
                    unselectedColor = Color(0xFF606060)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRoleSelectionScreen() {
    RoleSelectionScreen(
        navController = rememberNavController(),
        userViewModel = UserViewModel()
    )
}