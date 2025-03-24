package com.example.brickshare.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.brickshare.screens.*
import com.example.brickshare.viewmodel.UserViewModel

@Composable
fun Navigation(userViewModel: UserViewModel = viewModel()) {
    val navController = rememberNavController()
    val userRole by userViewModel.userRole.collectAsState()

    // Always start at welcome screen
    val startDestination = "welcome"

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = userRole != null,
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
            composable("welcome") { WelcomeScreen(navController) }
            composable("signin") { SignInScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("role_selection") { RoleSelectionScreen(navController, userViewModel) }
            composable("dashboard") { DashboardScreen(navController, userViewModel) }
            composable("browse") { BrowseScreen(navController) }
            composable("portfolio") { PortfolioScreen(navController) }
            composable("income") { IncomeScreen(navController) }
            composable("profile") { ProfileScreen(navController) }
            composable("property_detail/{propertyId}") { backStackEntry ->
                val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                PropertyDetailScreen(navController, propertyId)
            }
            composable("add_property") { AddPropertyScreen(navController) }
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