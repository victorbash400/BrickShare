package com.example.brickshare.navigation

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.brickshare.ui.theme.BrickShareFonts
import com.example.brickshare.viewmodel.UserViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.AddCircleOutline

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val badgeCount: Int = 0
)

@Composable
fun EnhancedBottomNavigationBar(
    navController: NavController,
    userViewModel: UserViewModel,
    backgroundColor: Color = Color(0xFF000000), // Pure black background
    selectedItemColor: Color = Color.White, // White for selected icons
    unselectedItemColor: Color = Color.White // White for unselected icons
) {
    val userRole by userViewModel.userRole.collectAsState()

    val items = when (userRole) {
        "investor" -> listOf(
            BottomNavItem("dashboard", Icons.Filled.Home, Icons.Filled.Home), // Home button
            BottomNavItem("browse", Icons.Filled.Search, Icons.Filled.Search),
            BottomNavItem("portfolio", Icons.Filled.AccountBalanceWallet, Icons.Filled.AccountBalanceWallet, badgeCount = 3),
            BottomNavItem("income", Icons.Filled.AttachMoney, Icons.Filled.AttachMoney),
            BottomNavItem("profile", Icons.Rounded.AccountCircle, Icons.Rounded.AccountCircle)
        )
        "property_owner" -> listOf(
            BottomNavItem("dashboard", Icons.Filled.Home, Icons.Filled.Home), // Home button
            BottomNavItem("add_property", Icons.Rounded.AddCircleOutline, Icons.Rounded.AddCircle),
            BottomNavItem("manage_property", Icons.Filled.Settings, Icons.Filled.Settings),
            BottomNavItem("profile", Icons.Rounded.AccountCircle, Icons.Rounded.AccountCircle)
        )
        else -> emptyList()
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.split("/")?.get(0) // Match base route

    Surface(
        color = backgroundColor,
        // Removed shape parameter to eliminate rounded corners
        tonalElevation = 0.dp, // No elevation for seamless integration
        shadowElevation = 0.dp, // No shadow either
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route

                val scale by animateFloatAsState(
                    targetValue = if (selected) 1.15f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "scale"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .scale(scale)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.icon,
                            contentDescription = item.route,
                            tint = if (selected) selectedItemColor else unselectedItemColor,
                            modifier = Modifier.size(24.dp)
                        )

                        if (item.badgeCount > 0) {
                            Badge(
                                modifier = Modifier
                                    .offset(x = 8.dp, y = (-8).dp)
                                    .size(16.dp)
                                    .align(Alignment.TopEnd),
                                containerColor = Color(0xFFE57373), // Soft red badge
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = item.badgeCount.toString(),
                                    fontSize = 9.sp,
                                    fontFamily = BrickShareFonts.Halcyon,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    if (selected) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE57373)) // Red dot for selected
                                .align(Alignment.BottomCenter)
                                .offset(y = (-2).dp)
                        )
                    }
                }
            }
        }
    }
}