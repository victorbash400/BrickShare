package com.example.brickshare.navigation

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
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
import com.example.brickshare.screens.*
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define routes where the bottom navigation bar should be visible
    val mainAppRoutes = listOf(
        "dashboard",
        "browse",
        "portfolio",
        "income",
        "profile",
        "add_property"
    )
    val isBottomBarVisible = userRole != null && (
            currentRoute in mainAppRoutes ||
                    currentRoute?.startsWith("manage_property/") == true
            )

    // Set the starting screen
    val startDestination = "welcome"

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                EnhancedBottomNavigationBar(navController, userViewModel)
            }
        }
    ) { innerPadding ->
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
                ProfileScreen(navController)
            }
            composable("property_detail/{propertyId}") { backStackEntry ->
                val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                PropertyDetailScreen(navController, propertyId)
            }
            composable("add_property") {
                AddPropertyScreen(navController)
            }
            composable("manage_property/{propertyId}") { backStackEntry ->
                val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                ManagePropertyScreen(navController, propertyId)
            }
            composable("buy_shares/{propertyId}") { backStackEntry ->
                val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                BuySharesScreen(navController, propertyId)
            }
        }
    }
}

fun NavHostController.navigateToMainApp() {
    navigate("dashboard") {
        popUpTo("welcome") { inclusive = true }
    }
}