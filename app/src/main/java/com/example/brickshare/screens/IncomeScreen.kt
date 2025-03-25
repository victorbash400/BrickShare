package com.example.brickshare.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
fun IncomeScreen(navController: NavController) {
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
                            "Income Summary",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = DeepNavy
                            )
                        }
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Added scrolling here
                verticalArrangement = Arrangement.Top
            ) {
                // Total Income Summary Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
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
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Total Monthly Income",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 18.sp,
                            color = DeepNavy.copy(alpha = 0.7f)
                        )
                        Text(
                            "$175",
                            style = TextStyle(
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowUp,
                                contentDescription = "Trending Up",
                                tint = HederaGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "+$15 from last month",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 14.sp,
                                color = HederaGreen
                            )
                        }
                    }
                }

                // Bar Chart Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                        Text(
                            "Monthly Income",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            MonthBar(month = "Aug", height = 0.5f, value = "$45")
                            MonthBar(month = "Sep", height = 0.65f, value = "$60")
                            MonthBar(month = "Oct", height = 0.72f, value = "$65")
                            MonthBar(month = "Nov", height = 0.85f, value = "$80")
                            MonthBar(month = "Dec", height = 1f, value = "$90")
                            MonthBar(month = "Jan", height = 0.95f, value = "$85")
                        }
                    }
                }

                // Income Projection Card
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = HederaGreen.copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(1.dp, HederaGreen.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Income Projection",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowUp,
                                contentDescription = "Projection",
                                tint = HederaGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Next 3 months: $150",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Based on your current investments and market performance",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 14.sp,
                            color = DeepNavy.copy(alpha = 0.7f)
                        )
                    }
                }

                // Income History Section
                Text(
                    "Income History",
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Income History List
                IncomeHistoryItem(month = "Oct 2023", amount = 50.0)
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = DeepNavy.copy(alpha = 0.2f))
                IncomeHistoryItem(month = "Nov 2023", amount = 55.0)
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = DeepNavy.copy(alpha = 0.2f))
                IncomeHistoryItem(month = "Dec 2023", amount = 60.0)
            }
        }
    }
}

@Composable
fun MonthBar(month: String, height: Float, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 12.sp,
            color = DeepNavy,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .width(30.dp)
                .height((100 * height).dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(HederaGreen.copy(alpha = 0.8f))
        )
        Text(
            month,
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 12.sp,
            color = DeepNavy,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun IncomeHistoryItem(month: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            month,
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 16.sp,
            color = DeepNavy
        )
        Text(
            "$${String.format("%.2f", amount)}",
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DeepNavy
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIncomeScreen() {
    IncomeScreen(navController = rememberNavController())
}