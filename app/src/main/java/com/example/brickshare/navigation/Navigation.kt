package com.example.brickshare.navigation

import AdaptiveBottomNavigationBar
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.brickshare.screens.AddPropertyScreen
import com.example.brickshare.screens.BrowseScreen
import com.example.brickshare.screens.BuySharesScreen
import com.example.brickshare.screens.DashboardScreen
import com.example.brickshare.screens.IncomeScreen
import com.example.brickshare.screens.ManagePropertyScreen
import com.example.brickshare.screens.ManageSinglePropertyScreen
import com.example.brickshare.screens.PortfolioScreen
import com.example.brickshare.screens.ProfileScreen
import com.example.brickshare.screens.PropertyDetailScreen
import com.example.brickshare.screens.RoleSelectionScreen
import com.example.brickshare.screens.WelcomeScreen
import com.example.brickshare.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@Composable
fun Navigation(
    userViewModel: UserViewModel,
    signInLauncher: ActivityResultLauncher<android.content.Intent>,
    googleSignInClient: GoogleSignInClient
) {
    val navController = rememberNavController()
    val userRole by userViewModel.userRole.collectAsState()
    val userId by userViewModel.userId.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.split("/")?.firstOrNull()

    // Routes where bottom navigation should be visible
    val mainAppRoutes = listOf(
        "dashboard",
        "browse",
        "portfolio",
        "income",
        "profile",
        "add_property",
        "manage_property"
    )

    val isBottomBarVisible = userRole != null && currentRoute in mainAppRoutes

    val startDestination = "welcome"

    // Use WindowInsets-aware Scaffold
    Scaffold(
        // Configure Scaffold to respect system window insets
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                // Use the new AdaptiveBottomNavigationBar instead
                AdaptiveBottomNavigationBar(navController, userViewModel)
            }
        }
    ) { innerPadding ->
        // Apply padding from Scaffold
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("welcome") {
                WelcomeScreen(navController, signInLauncher, googleSignInClient)
            }
            composable("role_selection") {
                RoleSelectionScreen(navController, userViewModel)
            }
            composable("dashboard") {
                DashboardScreen(navController, userViewModel)
            }
            composable("browse") {
                BrowseScreen(navController)
            }
            composable("portfolio") {
                PortfolioScreen(navController)
            }
            composable("income") {
                IncomeScreen(navController)
            }
            composable("profile") {
                ProfileScreen(navController, userViewModel)
            }
            composable("property_detail/{propertyId}") { backStackEntry ->
                val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                PropertyDetailScreen(navController, propertyId, userViewModel)
            }
            composable("add_property") {
                AddPropertyScreen(navController)
            }
            composable("manage_property") {
                if (userId != null) {
                    ManagePropertyScreen(navController, "", userViewModel)
                } else {
                    navController.navigate("welcome")
                }
            }
            composable("manage_property/{propertyId}") { backStackEntry ->
                val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                if (userId != null) {
                    ManageSinglePropertyScreen(navController, propertyId, userViewModel)
                } else {
                    navController.navigate("welcome")
                }
            }
            composable("buy_shares/{propertyId}") { backStackEntry ->
                val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                BuySharesScreen(navController, propertyId)
            }
        }
    }
}

/**
 * Extension function for navigating to the main app flow and clearing the back stack
 */
fun NavHostController.navigateToMainApp() {
    navigate("dashboard") {
        popUpTo("welcome") { inclusive = true }
    }
}