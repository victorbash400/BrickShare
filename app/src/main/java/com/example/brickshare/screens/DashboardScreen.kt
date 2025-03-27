package com.example.brickshare.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import com.example.brickshare.ui.theme.*
import com.example.brickshare.viewmodel.UserViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val userRole by userViewModel.userRole.collectAsState()
    val userId by userViewModel.userId.collectAsState()
    val scope = rememberCoroutineScope()
    var hbarBalance by remember { mutableStateOf(0.0) }
    var tokenBalances by remember { mutableStateOf<Map<String, Long>>(emptyMap()) }
    var properties by remember { mutableStateOf<List<Triple<String, String, String>>>(emptyList()) }
    val db = Firebase.firestore

    // Fetch user and property data
    LaunchedEffect(userId) {
        scope.launch {
            if (userId != null) {
                // Fetch user data
                val userDoc = db.collection("users").document(userId!!).get().await()
                hbarBalance = userDoc.getLong("hbarBalance")?.toDouble() ?: 0.0
                tokenBalances = userDoc.get("tokenBalances") as? Map<String, Long> ?: emptyMap()

                // Fetch only properties owned by the logged-in user
                val propertiesSnapshot = db.collection("properties")
                    .whereEqualTo("ownerId", userId)
                    .get()
                    .await()
                properties = propertiesSnapshot.documents.mapNotNull { propDoc ->
                    val propertyId = propDoc.id
                    val address = propDoc.getString("metadata.address") ?: "Unknown"
                    val pricePerToken = propDoc.getDouble("pricePerToken")?.toString() ?: "0.5"
                    Triple(propertyId, address, "$pricePerToken HBAR/share")
                }
            }
        }
    }

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
                        text = "Welcome back!",
                        style = TextStyle(
                            fontFamily = BrickShareFonts.Halcyon,
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

                WalletCard(userRole, hbarBalance, tokenBalances, navController)
                Spacer(Modifier.height(16.dp))
                BudgetCircle(userRole, hbarBalance)
                Spacer(Modifier.height(16.dp))
                WatchlistSection(userRole, properties, navController)
                Spacer(Modifier.height(16.dp))
                HistorySection(navController)
            }
        }
    }
}

// Rest of the file (WalletCard, BudgetCircle, etc.) remains unchanged

@Composable
fun WalletCard(userRole: String?, hbarBalance: Double, tokenBalances: Map<String, Long>, navController: NavController) {
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
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 16.sp,
                        color = DeepNavy.copy(alpha = 0.7f)
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (userRole == "investor") "$hbarBalance HBAR" else "${tokenBalances.values.sum()} Shares",
                    style = TextStyle(
                        fontFamily = BrickShareFonts.Halcyon,
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
fun BudgetCircle(userRole: String?, hbarBalance: Double) {
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
                    text = "$hbarBalance HBAR",
                    style = TextStyle(
                        fontFamily = BrickShareFonts.Halcyon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = DeepNavy
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (userRole == "investor") "Available Balance" else "Total Funds",
                    style = TextStyle(
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 14.sp,
                        color = DeepNavy.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

@Composable
fun WatchlistSection(userRole: String?, properties: List<Triple<String, String, String>>, navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (userRole == "investor") "Featured Properties" else "My Properties",
                style = TextStyle(
                    fontFamily = BrickShareFonts.Halcyon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = DeepNavy
                )
            )
            Text(
                text = "See All",
                style = TextStyle(
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 16.sp,
                    color = HederaGreen
                ),
                modifier = Modifier.clickable { navController.navigate("browse") }
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(properties) { (id, address, price) ->
                WatchlistCard(address, "N/A", price) {
                    navController.navigate("property_detail/$id")
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
                    fontFamily = BrickShareFonts.Halcyon,
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
                    text = "Progress: $metric",
                    style = TextStyle(
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 14.sp,
                        color = HederaGreen
                    )
                )
                Text(
                    text = value,
                    style = TextStyle(
                        fontFamily = BrickShareFonts.Halcyon,
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
                    fontFamily = BrickShareFonts.Halcyon,
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

        HistoryItem("bashtube400", "Hedera Wallet", "2 Shares")
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
                    fontFamily = BrickShareFonts.Halcyon,
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
                    fontFamily = BrickShareFonts.Halcyon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepNavy
                )
            )
            Text(
                text = method,
                style = TextStyle(
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 14.sp,
                    color = DeepNavy.copy(alpha = 0.7f)
                )
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = amount,
            style = TextStyle(
                fontFamily = BrickShareFonts.Halcyon,
                fontSize = 16.sp,
                color = DeepNavy
            )
        )
    }
}