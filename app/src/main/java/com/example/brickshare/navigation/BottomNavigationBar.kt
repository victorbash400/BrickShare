import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AddHome
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
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
fun AdaptiveBottomNavigationBar(
    navController: NavController,
    userViewModel: UserViewModel,
    backgroundColor: Color = Color(0xFF000000),
    selectedItemColor: Color = Color.White,
    unselectedItemColor: Color = Color.White.copy(alpha = 0.7f)
) {
    val userRole by userViewModel.userRole.collectAsState()
    val density = LocalDensity.current

    // Get system navigation insets
    val navigationBarInsets = WindowInsets.navigationBars
    val hasNavigationBar = remember {
        navigationBarInsets.getBottom(density) > 0
    }

    // Adjust scale factor based on device
    val maxScaleFactor = if (hasNavigationBar) 1.1f else 1.2f

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
    val currentDestination = navBackStackEntry?.destination

    // Get base route without parameters
    val currentRoute = remember(currentDestination) {
        currentDestination?.route?.split("/")?.firstOrNull() ?: ""
    }

    // Apply bottom padding based on system insets
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        NavigationBar(
            containerColor = backgroundColor,
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(bottom = if (hasNavigationBar) bottomPadding else 0.dp)
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
                        targetValue = if (selected) maxScaleFactor else 1f,
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
                                    // Pop up to the start destination to avoid building a large back stack
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
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
                                        modifier = Modifier.size(26.dp) // Slightly smaller to prevent overlap
                                    )
                                    if (item.badgeCount > 0) {
                                        Badge(
                                            modifier = Modifier
                                                .offset(x = 10.dp, y = (-10).dp)
                                                .size(16.dp),
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
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(HederaGreen)
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
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}