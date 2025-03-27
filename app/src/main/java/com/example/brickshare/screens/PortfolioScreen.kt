package com.example.brickshare.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
    val coroutineScope = rememberCoroutineScope()

    // Fetch user token balances and properties
    LaunchedEffect(userId) {
        if (userId != null) {
            coroutineScope.launch {
                // Fetch user's token balances
                val userDoc = db.collection("users").document(userId).get().await()
                tokenBalances = userDoc.get("tokenBalances") as? Map<String, Long> ?: emptyMap()

                // Fetch all properties
                val propertiesSnapshot = db.collection("properties").get().await()
                properties = propertiesSnapshot.documents.associate { doc ->
                    doc.id to doc.data!!
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = HederaGreen,
            onPrimary = Color.White,
            background = BuildingBlocksWhite,
            onBackground = DeepNavy
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
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BuildingBlocksWhite
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                // Portfolio Summary Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                        contentColor = DeepNavy
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Total Value",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 18.sp,
                                color = DeepNavy
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.KeyboardArrowUp,
                                    contentDescription = "Up",
                                    tint = HederaGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    "7.2%", // Placeholder; calculate dynamically if data available
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 14.sp,
                                    color = HederaGreen
                                )
                            }
                        }

                        // Calculate total value dynamically
                        val totalValue = tokenBalances.entries.sumOf { (propertyId, shares) ->
                            val pricePerToken = (properties[propertyId]?.get("pricePerToken") as? Number)?.toDouble() ?: 0.0
                            shares * pricePerToken
                        }
                        Text(
                            "$${String.format("%.2f", totalValue)}",
                            style = TextStyle(
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Placeholder for Line Chart
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(HederaGreen.copy(alpha = 0.1f))
                        ) {
                            Text(
                                "[Portfolio Value Chart]",
                                modifier = Modifier.align(Alignment.Center),
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 14.sp,
                                color = HederaGreen,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Investments Section
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
                    TextButton(onClick = { /* View all investments */ }) {
                        Text(
                            "View All",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 16.sp,
                            color = HederaGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dynamic Property Cards
                if (tokenBalances.isEmpty()) {
                    Text(
                        "No investments yet",
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 16.sp,
                        color = DeepNavy.copy(alpha = 0.7f)
                    )
                } else {
                    tokenBalances.forEach { (propertyId, shares) ->
                        val propertyData = properties[propertyId]
                        if (propertyData != null) {
                            val name = propertyData["metadata.address"] as? String ?: "Unknown Property"
                            val pricePerToken = (propertyData["pricePerToken"] as? Number)?.toDouble() ?: 0.0
                            val value = shares * pricePerToken
                            val roi = 5.0 // Placeholder; fetch real ROI if available

                            PropertyCard(
                                name = name,
                                shares = shares.toInt(),
                                value = value,
                                roi = roi,
                                onBuyMore = { navController.navigate("buyShares/$propertyId") }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Income Section (Placeholder)
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, DeepNavy.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Monthly Income",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 18.sp,
                                color = DeepNavy
                            )
                            Text(
                                "$175", // Placeholder; calculate dynamically if data available
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            )
                        }

                        Button(
                            onClick = { navController.navigate("income") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HederaGreen
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Income Details",
                                fontFamily = BrickShareFonts.Halcyon,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PropertyCard(
    name: String,
    shares: Int,
    value: Double,
    roi: Double,
    onBuyMore: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = DeepNavy
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    name,
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "$shares shares | $${String.format("%.2f", value)} | $roi% ROI",
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 14.sp,
                    color = DeepNavy.copy(alpha = 0.7f)
                )
            }

            Button(
                onClick = onBuyMore,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HederaGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Buy More",
                    fontFamily = BrickShareFonts.Halcyon,
                    color = Color.White
                )
            }
        }
    }
}