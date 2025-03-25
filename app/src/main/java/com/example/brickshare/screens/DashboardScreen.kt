package com.example.brickshare.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.brickshare.ui.theme.BrickShareFonts.Halcyon
import com.example.brickshare.ui.theme.*
import com.example.brickshare.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val userRole by userViewModel.userRole.collectAsState()

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = HederaGreen,
            onPrimary = Color.White,
            background = BuildingBlocksWhite,
            onBackground = DeepNavy,
            surface = Color.White,
            onSurface = DeepNavy
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BuildingBlocksWhite
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Welcome back, Jesse!",
                        style = TextStyle(
                            fontFamily = Halcyon,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = DeepNavy
                        )
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = { /* TODO: Navigate to search */ },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(HederaGreen.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = HederaGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                WalletCard(userRole, navController)
                Spacer(Modifier.height(16.dp))
                BudgetCircle(userRole)
                Spacer(Modifier.height(16.dp))
                WatchlistSection(userRole, navController)
                Spacer(Modifier.height(16.dp))
                HistorySection(navController)
            }
        }
    }
}

@Composable
fun WalletCard(userRole: String?, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = DeepNavy,
            disabledContainerColor = NavyGrey,
            disabledContentColor = Color.LightGray
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (userRole == "investor") "Portfolio Value" else "Property Value",
                    style = TextStyle(
                        fontFamily = Halcyon,
                        fontSize = 16.sp,
                        color = DeepNavy.copy(alpha = 0.7f)
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (userRole == "investor") "$124,500" else "$500,000",
                    style = TextStyle(
                        fontFamily = Halcyon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = DeepNavy
                    )
                )
            }
            Spacer(Modifier.weight(1f))
            Icon(
                Icons.Rounded.Add,
                contentDescription = "Add",
                tint = HederaGreen,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { navController.navigate("browse") }
            )
        }
    }
}

@Composable
fun BudgetCircle(userRole: String?) {
    Card(
        modifier = Modifier
            .size(150.dp),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = DeepNavy,
            disabledContainerColor = NavyGrey,
            disabledContentColor = Color.LightGray
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (userRole == "investor") "$12,423" else "$8,900",
                    style = TextStyle(
                        fontFamily = Halcyon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = DeepNavy
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Total Gains",
                    style = TextStyle(
                        fontFamily = Halcyon,
                        fontSize = 14.sp,
                        color = DeepNavy.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

@Composable
fun WatchlistSection(userRole: String?, navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (userRole == "investor") "Watchlist" else "Properties",
                style = TextStyle(
                    fontFamily = Halcyon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = DeepNavy
                )
            )
            Text(
                text = "See All",
                style = TextStyle(
                    fontFamily = Halcyon,
                    fontSize = 16.sp,
                    color = HederaGreen
                ),
                modifier = Modifier.clickable { navController.navigate("browse") }
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(
                if (userRole == "investor") {
                    listOf(
                        Triple("Beach Villa", "+12.5%", "$15,000"),
                        Triple("City Condo", "+8.2%", "$10,500"),
                        Triple("Modern Loft", "-2.1%", "$8,200")
                    )
                } else {
                    listOf(
                        Triple("Beach Villa", "92%", "$2,100/mo"),
                        Triple("City Condo", "85%", "$800/mo"),
                        Triple("Modern Loft", "78%", "$1,200/mo")
                    )
                }
            ) { (name, metric, value) ->
                WatchlistCard(name, metric, value) {
                    navController.navigate("property_detail/0")
                }
            }
        }
    }
}

@Composable
fun WatchlistCard(name: String, metric: String, value: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = DeepNavy,
            disabledContainerColor = NavyGrey,
            disabledContentColor = Color.LightGray
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = name,
                style = TextStyle(
                    fontFamily = Halcyon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepNavy
                )
            )
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = metric,
                    style = TextStyle(
                        fontFamily = Halcyon,
                        fontSize = 14.sp,
                        color = if (metric.startsWith("+")) HederaGreen else Color.Red
                    )
                )
                Text(
                    text = value,
                    style = TextStyle(
                        fontFamily = Halcyon,
                        fontSize = 14.sp,
                        color = DeepNavy.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

@Composable
fun HistorySection(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "History",
                style = TextStyle(
                    fontFamily = Halcyon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = DeepNavy
                )
            )
            Icon(
                Icons.Rounded.ArrowForward,
                contentDescription = "See More",
                tint = HederaGreen,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.navigate("history") }
            )
        }

        Spacer(Modifier.height(8.dp))

        HistoryItem("Jordan", "MasterCard Debit", "0.00045 BTC")
        HistoryItem("Uber", "Bitcoin Wallet", "$160")
    }
}

@Composable
fun HistoryItem(name: String, method: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name[0].toString(),
                style = TextStyle(
                    fontFamily = Halcyon,
                    fontSize = 16.sp,
                    color = DeepNavy
                )
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = name,
                style = TextStyle(
                    fontFamily = Halcyon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepNavy
                )
            )
            Text(
                text = method,
                style = TextStyle(
                    fontFamily = Halcyon,
                    fontSize = 14.sp,
                    color = DeepNavy.copy(alpha = 0.7f)
                )
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = amount,
            style = TextStyle(
                fontFamily = Halcyon,
                fontSize = 16.sp,
                color = DeepNavy
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreviewInvestor() {
    val viewModel = UserViewModel().apply { setUserRole("investor") }
    DashboardScreen(rememberNavController(), viewModel)
}

@Preview(showBackground = true)
@Composable
fun DashboardPreviewOwner() {
    val viewModel = UserViewModel().apply { setUserRole("owner") }
    DashboardScreen(rememberNavController(), viewModel)
}