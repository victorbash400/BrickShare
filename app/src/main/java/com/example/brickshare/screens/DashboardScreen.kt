package com.example.brickshare.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brickshare.ui.theme.*
import com.example.brickshare.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
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
    val username = FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0) ?: "User"

    LaunchedEffect(userId, userRole) {
        scope.launch {
            if (userId != null) {
                // Fetch user data
                val userDoc = db.collection("users").document(userId!!).get().await()
                hbarBalance = userDoc.getLong("hbarBalance")?.toDouble() ?: 0.0
                tokenBalances = userDoc.get("tokenBalances") as? Map<String, Long> ?: emptyMap()

                // Fetch properties based on role
                val propertiesSnapshot = if (userRole == "investor") {
                    // Show all properties for investors (like BrowseScreen)
                    db.collection("properties")
                        .orderBy("propertyId", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .get()
                        .await()
                } else {
                    // Show only owned properties for owners
                    db.collection("properties")
                        .whereEqualTo("ownerId", userId)
                        .get()
                        .await()
                }
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
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DashboardHeader(username, navController)
                Spacer(modifier = Modifier.height(20.dp))
                WalletCard(userRole, hbarBalance, tokenBalances, navController)
                Spacer(modifier = Modifier.height(24.dp))
                BudgetCircle(userRole, hbarBalance)
                Spacer(modifier = Modifier.height(24.dp))
                WatchlistSection(userRole, properties, navController)
                Spacer(modifier = Modifier.height(32.dp))
                ChangeRoleButton(navController)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun DashboardHeader(username: String, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Welcome, $username!",
            style = TextStyle(
                fontFamily = BrickShareFonts.Halcyon,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = DeepNavy
            ),
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = { navController.navigate("profile") },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(HederaGreen.copy(alpha = 0.15f))
        ) {
            Icon(
                Icons.Rounded.AccountCircle,
                contentDescription = "Profile",
                tint = HederaGreen,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun WalletCard(
    userRole: String?,
    hbarBalance: Double,
    tokenBalances: Map<String, Long>,
    navController: NavController
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
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
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (userRole == "investor") "Portfolio Value" else "Property Value",
                    style = TextStyle(
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 18.sp,
                        color = DeepNavy.copy(alpha = 0.7f)
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (userRole == "investor") "$hbarBalance HBAR" else "${tokenBalances.values.sum()} Shares",
                    style = TextStyle(
                        fontFamily = BrickShareFonts.Halcyon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = DeepNavy
                    )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { navController.navigate("browse") },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(HederaGreen.copy(alpha = 0.15f))
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Add",
                    tint = HederaGreen,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun BudgetCircle(userRole: String?, hbarBalance: Double) {
    Card(
        modifier = Modifier
            .size(160.dp)
            .shadow(8.dp, CircleShape),
        shape = CircleShape,
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
                        fontSize = 26.sp,
                        color = DeepNavy
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (userRole == "investor") "Available Balance" else "Total Funds",
                    style = TextStyle(
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 16.sp,
                        color = DeepNavy.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

@Composable
private fun WatchlistSection(
    userRole: String?,
    properties: List<Triple<String, String, String>>,
    navController: NavController
) {
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
                    fontSize = 22.sp,
                    color = DeepNavy
                )
            )
            Text(
                text = "Browse All",
                style = TextStyle(
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 16.sp,
                    color = HederaGreen
                ),
                modifier = Modifier.clickable { navController.navigate("browse") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(properties) { (id, address, price) ->
                WatchlistCard(address, "N/A", price) {
                    if (userRole == "investor") {
                        navController.navigate("property_detail/$id")
                    } else {
                        navController.navigate("manage_property")
                    }
                }
            }
        }
    }
}

@Composable
private fun WatchlistCard(name: String, metric: String, value: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = DeepNavy,
            disabledContainerColor = NavyGrey,
            disabledContentColor = Color.LightGray
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = name,
                style = TextStyle(
                    fontFamily = BrickShareFonts.Halcyon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DeepNavy
                ),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Progress: $metric",
                style = TextStyle(
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 14.sp,
                    color = HederaGreen
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
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

@Composable
private fun ChangeRoleButton(navController: NavController) {
    ElevatedButton(
        onClick = { navController.navigate("role_selection") },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = HederaGreen,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Switch Role",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = BrickShareFonts.Halcyon
            )
        }
    }
}