package com.example.brickshare.navigation

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import com.example.brickshare.ui.theme.HederaGreen
import com.example.brickshare.viewmodel.UserViewModel

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
    unselectedItemColor: Color = Color.White.copy(alpha = 0.7f) // Slightly dimmed white for unselected
) {
    val userRole by userViewModel.userRole.collectAsState()

    val items = when (userRole) {
        "investor" -> listOf(
            BottomNavItem("dashboard", Icons.Rounded.Home, Icons.Rounded.Home),
            BottomNavItem("browse", Icons.Rounded.Explore, Icons.Rounded.Explore),
            BottomNavItem("portfolio", Icons.Rounded.PieChart, Icons.Rounded.PieChart),
            BottomNavItem("income", Icons.Rounded.TrendingUp, Icons.Rounded.TrendingUp),
            BottomNavItem("profile", Icons.Rounded.Person, Icons.Rounded.Person)
        )
        "property_owner" -> listOf(
            BottomNavItem("dashboard", Icons.Rounded.Home, Icons.Rounded.Home),
            BottomNavItem("add_property", Icons.Rounded.AddHome, Icons.Rounded.AddHome),
            BottomNavItem("manage_property", Icons.Rounded.Apartment, Icons.Rounded.Apartment),
            BottomNavItem("profile", Icons.Rounded.Person, Icons.Rounded.Person)
        )
        else -> emptyList()
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.split("/")?.get(0) // Match base route

    NavigationBar(
        containerColor = backgroundColor,
        tonalElevation = 0.dp, // Flat look
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp) // Slightly taller for modern feel
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route

                val scale by animateFloatAsState(
                    targetValue = if (selected) 1.2f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "scale"
                )

                NavigationBarItem(
                    selected = selected,
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
                    icon = {
                        Column(
                            modifier = Modifier.scale(scale),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.icon,
                                    contentDescription = item.route,
                                    tint = if (selected) selectedItemColor else unselectedItemColor,
                                    modifier = Modifier.size(28.dp) // Larger icons for modern look
                                )
                                if (item.badgeCount > 0) {
                                    Badge(
                                        modifier = Modifier
                                            .offset(x = 10.dp, y = (-10).dp)
                                            .size(18.dp),
                                        containerColor = Color(0xFFE57373),
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = item.badgeCount.toString(),
                                            fontSize = 10.sp,
                                            fontFamily = BrickShareFonts.Halcyon,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            if (selected) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(HederaGreen) // Green dot under selected icon
                                        .padding(top = 2.dp) // Small spacing from icon
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = selectedItemColor,
                        unselectedIconColor = unselectedItemColor,
                        indicatorColor = Color.Transparent // Remove default indicator
                    )
                )
            }
        }
    }
}