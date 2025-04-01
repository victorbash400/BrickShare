package com.example.brickshare.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brickshare.HederaUtils
import com.example.brickshare.ui.theme.BrickShareFonts
import com.example.brickshare.ui.theme.HederaGreen
import com.example.brickshare.ui.theme.DeepNavy
import com.example.brickshare.ui.theme.BuildingBlocksWhite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(navController: NavController) {
    val db = Firebase.firestore
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var tokenBalances by remember { mutableStateOf<Map<String, Long>>(emptyMap()) }
    var properties by remember { mutableStateOf<Map<String, Map<String, Any>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var sortOption by remember { mutableStateOf("Value: High to Low") }

    LaunchedEffect(userId, sortOption) {
        if (userId != null) {
            coroutineScope.launch {
                isLoading = true
                try {
                    val userDoc = db.collection("users").document(userId).get().await()
                    val hederaAccountId = userDoc.getString("hederaAccountId") ?: return@launch
                    tokenBalances = HederaUtils.fetchTokenBalances(hederaAccountId)
                    HederaUtils.syncTokenBalances(userId, hederaAccountId)

                    val propertiesSnapshot = db.collection("properties").get().await()
                    properties = propertiesSnapshot.documents.associate { doc ->
                        doc.id to doc.data!!
                    }
                    errorMessage = null
                } catch (e: Exception) {
                    errorMessage = "Error loading portfolio: ${e.message}"
                } finally {
                    isLoading = false
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
            containerColor = BuildingBlocksWhite,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Your Portfolio",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            if (userId != null) {
                                coroutineScope.launch {
                                    isLoading = true
                                    try {
                                        val hederaAccountId = db.collection("users").document(userId).get().await()
                                            .getString("hederaAccountId") ?: return@launch
                                        tokenBalances = HederaUtils.fetchTokenBalances(hederaAccountId)
                                        HederaUtils.syncTokenBalances(userId, hederaAccountId)
                                        errorMessage = null
                                    } catch (e: Exception) {
                                        errorMessage = "Error refreshing: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        }) {
                            Icon(
                                Icons.Rounded.Refresh,
                                contentDescription = "Refresh",
                                tint = HederaGreen
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BuildingBlocksWhite),
                    modifier = Modifier.shadow(4.dp)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(BuildingBlocksWhite, HederaGreen.copy(alpha = 0.05f))
                        )
                    )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 50.dp))
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 16.sp,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 50.dp)
                    )
                } else {
                    PortfolioSummaryCard(tokenBalances, properties)
                    Spacer(modifier = Modifier.height(24.dp))
                    InvestmentsSection(tokenBalances, properties, navController, sortOption) { sortOption = it }
                    Spacer(modifier = Modifier.height(24.dp))
                    IncomeCard(tokenBalances, properties, navController)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun PortfolioSummaryCard(tokenBalances: Map<String, Long>, properties: Map<String, Map<String, Any>>) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
    )

    val totalValue = tokenBalances.entries.sumOf { (propertyId, shares) ->
        val pricePerToken = (properties[propertyId]?.get("pricePerToken") as? Number)?.toDouble() ?: 0.0
        shares * pricePerToken
    }
    val totalIncome = tokenBalances.entries.sumOf { (propertyId, _) ->
        (properties[propertyId]?.get("totalHbarCollected") as? Number)?.toDouble() ?: 0.0
    } * 0.05

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .scale(scale)
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = DeepNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Portfolio Value",
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 20.sp,
                    color = DeepNavy.copy(alpha = 0.7f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Rounded.TrendingUp,
                        contentDescription = "Trend",
                        tint = HederaGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "+${String.format("%.1f", totalIncome / totalValue * 100)}%",
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 16.sp,
                        color = HederaGreen
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "${String.format("%.2f", totalValue)} HBAR",
                style = TextStyle(
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(HederaGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "[Value Trend Chart Placeholder]",
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 14.sp,
                    color = HederaGreen,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun InvestmentsSection(
    tokenBalances: Map<String, Long>,
    properties: Map<String, Map<String, Any>>,
    navController: NavController,
    sortOption: String,
    onSortChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Your Investments",
                fontFamily = BrickShareFonts.Halcyon,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DeepNavy
            )
            SortDropdown(sortOption, onSortChange)
        }
        Spacer(modifier = Modifier.height(12.dp))

        val sortedInvestments = when (sortOption) {
            "Value: High to Low" -> tokenBalances.entries.sortedByDescending { (id, shares) ->
                val price = (properties[id]?.get("pricePerToken") as? Number)?.toDouble() ?: 0.0
                shares * price
            }
            "Value: Low to High" -> tokenBalances.entries.sortedBy { (id, shares) ->
                val price = (properties[id]?.get("pricePerToken") as? Number)?.toDouble() ?: 0.0
                shares * price
            }
            "Shares: High to Low" -> tokenBalances.entries.sortedByDescending { it.value }
            "Shares: Low to High" -> tokenBalances.entries.sortedBy { it.value }
            else -> tokenBalances.entries.toList()
        }

        if (sortedInvestments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(HederaGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No investments yet. Start exploring properties!",
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 18.sp,
                    color = DeepNavy.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sortedInvestments) { (propertyId, shares) ->
                    val propertyData = properties[propertyId]
                    if (propertyData != null) {
                        // Safely access nested metadata map
                        val metadata = propertyData["metadata"] as? Map<*, *>
                        val name = metadata?.get("address") as? String ?: "Unknown Property"
                        val pricePerToken = (propertyData["pricePerToken"] as? Number)?.toDouble() ?: 0.0
                        val value = shares * pricePerToken
                        val totalHbarCollected = (propertyData["totalHbarCollected"] as? Number)?.toDouble() ?: 0.0
                        val roi = if (value > 0) (totalHbarCollected / value) * 100 else 0.0

                        PropertyCard(
                            name = name,
                            shares = shares.toInt(),
                            value = value,
                            roi = roi,
                            onBuyMore = { navController.navigate("buy_shares/$propertyId") },
                            onDetails = { navController.navigate("property_detail/$propertyId") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SortDropdown(sortOption: String, onSortChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(HederaGreen.copy(alpha = 0.15f))
        ) {
            Icon(
                Icons.Rounded.Sort,
                contentDescription = "Sort",
                tint = HederaGreen,
                modifier = Modifier.size(24.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            listOf("Value: High to Low", "Value: Low to High", "Shares: High to Low", "Shares: Low to High").forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 16.sp,
                            color = DeepNavy
                        )
                    },
                    onClick = {
                        onSortChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PropertyCard(
    name: String,
    shares: Int,
    value: Double,
    roi: Double,
    onBuyMore: () -> Unit,
    onDetails: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = DeepNavy)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$shares shares",
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 14.sp,
                        color = DeepNavy.copy(alpha = 0.7f)
                    )
                    Text(
                        "${String.format("%.2f", value)} HBAR",
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 14.sp,
                        color = HederaGreen,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "${String.format("%.1f", roi)}% ROI",
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 14.sp,
                        color = DeepNavy.copy(alpha = 0.7f)
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onDetails,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(HederaGreen.copy(alpha = 0.15f))
                ) {
                    Icon(
                        Icons.Rounded.Info,
                        contentDescription = "Details",
                        tint = HederaGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                ElevatedButton(
                    onClick = onBuyMore,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = HederaGreen,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Buy",
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun IncomeCard(
    tokenBalances: Map<String, Long>,
    properties: Map<String, Map<String, Any>>,
    navController: NavController
) {
    val monthlyIncome = tokenBalances.entries.sumOf { (propertyId, shares) ->
        val totalHbarCollected = (properties[propertyId]?.get("totalHbarCollected") as? Number)?.toDouble() ?: 0.0
        val totalSupply = (properties[propertyId]?.get("totalTokens") as? Number)?.toLong() ?: 1L
        (totalHbarCollected / totalSupply) * shares * 0.0833
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = DeepNavy)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Monthly Income",
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 20.sp,
                    color = DeepNavy.copy(alpha = 0.7f)
                )
                Text(
                    "${String.format("%.2f", monthlyIncome)} HBAR",
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )
            }
            ElevatedButton(
                onClick = { navController.navigate("income") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = HederaGreen,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    "Details",
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 16.sp
                )
            }
        }
    }
}