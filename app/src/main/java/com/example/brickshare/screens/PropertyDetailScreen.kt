package com.example.brickshare.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brickshare.R
import com.example.brickshare.ui.theme.BrickShareFonts
import com.example.brickshare.ui.theme.HederaGreen
import com.example.brickshare.ui.theme.DeepNavy
import com.example.brickshare.ui.theme.BuildingBlocksWhite
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(navController: NavController, propertyId: String) {
    var isFavorite by remember { mutableStateOf(false) }

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
                            "Property Details",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = DeepNavy
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Share functionality */ }) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = DeepNavy
                            )
                        }
                        IconButton(onClick = { isFavorite = !isFavorite }) {
                            Icon(
                                if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red else DeepNavy
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
                    .verticalScroll(rememberScrollState())
            ) {
                // Image Gallery with Pager
                Box(modifier = Modifier.height(250.dp)) {
                    val pagerState = rememberPagerState()

                    HorizontalPager(
                        count = 3,
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Image(
                            painter = painterResource(id = R.drawable.placeholder_property),
                            contentDescription = "Property Image ${page + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Pager Indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .background(
                                color = DeepNavy.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        HorizontalPagerIndicator(
                            pagerState = pagerState,
                            activeColor = HederaGreen,
                            inactiveColor = BuildingBlocksWhite.copy(alpha = 0.5f)
                        )
                    }
                }

                // Main Content
                Column(modifier = Modifier.padding(16.dp)) {
                    // Property Title and Address
                    Text(
                        "123 Main Street, Copenhagen",
                        style = TextStyle(
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    )

                    Text(
                        "Property ID: $propertyId",
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 14.sp,
                        color = DeepNavy.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatCard(title = "Price/Share", value = "$50", modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        StatCard(title = "Total Shares", value = "100", modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        StatCard(title = "ROI", value = "5%", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Map Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(HederaGreen.copy(alpha = 0.1f))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder_property),
                            contentDescription = "Property Location Map",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                                .background(
                                    DeepNavy.copy(alpha = 0.7f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "Copenhagen, Denmark",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 12.sp,
                                color = BuildingBlocksWhite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        "Description",
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepNavy
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Beautiful property located in the heart of Copenhagen. This prime real estate investment offers exceptional returns with minimal risk. The property has been fully renovated with premium materials and features modern amenities throughout.",
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 16.sp,
                        color = DeepNavy.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Property Features
                    PropertyFeaturesList()

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buy Shares Button
                    Button(
                        onClick = { navController.navigate("buy_shares/$propertyId") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HederaGreen
                        )
                    ) {
                        Text(
                            "Buy Shares",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = DeepNavy,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontFamily = BrickShareFonts.Halcyon,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = HederaGreen
            )
            Text(
                text = title,
                fontFamily = BrickShareFonts.Halcyon,
                fontSize = 14.sp,
                color = DeepNavy.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun PropertyFeaturesList() {
    Column {
        Text(
            "Property Features",
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DeepNavy
        )

        Spacer(modifier = Modifier.height(8.dp))

        PropertyFeatureItem("Size", "120 m²")
        PropertyFeatureItem("Bedrooms", "3")
        PropertyFeatureItem("Bathrooms", "2")
        PropertyFeatureItem("Year Built", "2015")
        PropertyFeatureItem("Expected Annual Return", "5-7%")
    }
}

@Composable
fun PropertyFeatureItem(feature: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = feature,
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 16.sp,
            color = DeepNavy.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DeepNavy
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPropertyDetailScreen() {
    PropertyDetailScreen(
        navController = rememberNavController(),
        propertyId = "1"
    )
}