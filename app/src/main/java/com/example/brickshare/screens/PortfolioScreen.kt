package com.example.brickshare.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(navController: NavController) {
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
                    colors = CardColors(
                        containerColor = Color.White,
                        contentColor = DeepNavy,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.LightGray
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
                                    "7.2%",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 14.sp,
                                    color = HederaGreen
                                )
                            }
                        }

                        Text(
                            "$34,750",
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

                // Property Cards
                PropertyCard(
                    name = "Sunset Tower",
                    shares = 5,
                    value = 250.0,
                    roi = 5.0,
                    onBuyMore = { /* Buy more shares */ }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PropertyCard(
                    name = "Downtown Heights",
                    shares = 3,
                    value = 180.0,
                    roi = 4.2,
                    onBuyMore = { /* Buy more shares */ }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Income Section
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
                                "$175",
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
        colors = CardColors(
            containerColor = Color.White,
            contentColor = DeepNavy,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
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

@Preview(showBackground = true)
@Composable
fun PreviewPortfolioScreen() {
    PortfolioScreen(navController = rememberNavController())
}