package com.example.brickshare.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.KeyboardArrowUp
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
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = DeepNavy,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BuildingBlocksWhite
                    ),
                    modifier = Modifier.shadow(4.dp)
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(BuildingBlocksWhite, HederaGreen.copy(alpha = 0.05f))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top
                ) {
                    TotalIncomeCard()
                    Spacer(modifier = Modifier.height(24.dp))
                    BarChartCard()
                    Spacer(modifier = Modifier.height(24.dp))
                    IncomeProjectionCard()
                    Spacer(modifier = Modifier.height(24.dp))
                    IncomeHistorySection()
                }

                // Placeholder Overlay
                PlaceholderOverlay()
            }
        }
    }
}

@Composable
private fun TotalIncomeCard() {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Total Monthly Income",
                fontFamily = BrickShareFonts.Halcyon,
                fontSize = 20.sp,
                color = DeepNavy.copy(alpha = 0.7f)
            )
            Text(
                "$175",
                style = TextStyle(
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                ),
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowUp,
                    contentDescription = "Trending Up",
                    tint = HederaGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "+$15 from last month",
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 16.sp,
                    color = HederaGreen
                )
            }
        }
    }
}

@Composable
private fun BarChartCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        shape = RoundedCornerShape(16.dp),
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
                .padding(20.dp)
        ) {
            Text(
                "Monthly Income",
                fontFamily = BrickShareFonts.Halcyon,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DeepNavy
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
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
}

@Composable
private fun MonthBar(month: String, height: Float, value: String) {
    val animatedHeight by animateFloatAsState(
        targetValue = height,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 14.sp,
            color = DeepNavy,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Box(
            modifier = Modifier
                .width(36.dp)
                .height((120 * animatedHeight).dp)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(HederaGreen, HederaGreen.copy(alpha = 0.6f))
                    )
                )
        )
        Text(
            month,
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 14.sp,
            color = DeepNavy,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun IncomeProjectionCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardColors(
            containerColor = HederaGreen.copy(alpha = 0.1f),
            contentColor = DeepNavy,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                "Income Projection",
                fontFamily = BrickShareFonts.Halcyon,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DeepNavy
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowUp,
                    contentDescription = "Projection",
                    tint = HederaGreen,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Next 3 months: $150",
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Based on current investments & market trends",
                fontFamily = BrickShareFonts.Halcyon,
                fontSize = 14.sp,
                color = DeepNavy.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun IncomeHistorySection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Income History",
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DeepNavy,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        IncomeHistoryItem(month = "Oct 2023", amount = 50.0)
        Divider(modifier = Modifier.padding(vertical = 8.dp), color = DeepNavy.copy(alpha = 0.2f))
        IncomeHistoryItem(month = "Nov 2023", amount = 55.0)
        Divider(modifier = Modifier.padding(vertical = 8.dp), color = DeepNavy.copy(alpha = 0.2f))
        IncomeHistoryItem(month = "Dec 2023", amount = 60.0)
    }
}

@Composable
private fun IncomeHistoryItem(month: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            month,
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 18.sp,
            color = DeepNavy
        )
        Text(
            "$${String.format("%.2f", amount)}",
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DeepNavy
        )
    }
}

@Composable
private fun PlaceholderOverlay() {
    val alpha by animateFloatAsState(
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(HederaGreen.copy(alpha = alpha))
                .padding(24.dp)
        ) {
            Text(
                "Income Page Preview",
                fontFamily = BrickShareFonts.Halcyon,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Coming Soon in Production!",
                fontFamily = BrickShareFonts.Halcyon,
                fontSize = 20.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIncomeScreen() {
    IncomeScreen(navController = rememberNavController())
}