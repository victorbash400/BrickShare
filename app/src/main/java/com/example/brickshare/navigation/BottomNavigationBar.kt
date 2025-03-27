package com.example.brickshare.navigation

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    backgroundColor: Color = Color(0xFF121212),
    selectedItemColor: Color = Color(0xFF90CAF9),
    unselectedItemColor: Color = Color(0xFFAAAAAA)
) {
    val userRole by userViewModel.userRole.collectAsState()

    val items = when (userRole) {
        "investor" -> listOf(
            BottomNavItem("dashboard", Icons.Rounded.Dashboard, Icons.Rounded.Dashboard),
            BottomNavItem("browse", Icons.Rounded.Explore, Icons.Rounded.Explore),
            BottomNavItem("portfolio", Icons.Rounded.Wallet, Icons.Rounded.Wallet, badgeCount = 3),
            BottomNavItem("income", Icons.Rounded.MonetizationOn, Icons.Rounded.MonetizationOn),
            BottomNavItem("profile", Icons.Rounded.AccountCircle, Icons.Rounded.AccountCircle)
        )
        "property_owner" -> listOf(
            BottomNavItem("dashboard", Icons.Rounded.Dashboard, Icons.Rounded.Dashboard),
            BottomNavItem("add_property", Icons.Rounded.AddCircleOutline, Icons.Rounded.AddCircle),
            BottomNavItem("manage_property/1", Icons.Rounded.Tune, Icons.Rounded.Tune),
            BottomNavItem("profile", Icons.Rounded.AccountCircle, Icons.Rounded.AccountCircle)
        )
        else -> emptyList()
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route

                val scale by animateFloatAsState(
                    targetValue = if (selected) 1.1f else 1f,
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
                            .size(36.dp)
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
                                    .offset(x = 10.dp, y = (-10).dp)
                                    .size(14.dp)
                                    .align(Alignment.TopEnd),
                                containerColor = Color(0xFFFF5722),
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = item.badgeCount.toString(),
                                    fontSize = 8.sp,
                                    fontFamily = BrickShareFonts.Halcyon,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    if (selected) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(selectedItemColor)
                                .align(Alignment.BottomCenter)
                                .offset(y = (-4).dp)
                        )
                    }
                }
            }
        }
    }
}