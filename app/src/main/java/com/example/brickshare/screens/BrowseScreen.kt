package com.example.brickshare.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brickshare.R
import com.example.brickshare.ui.theme.BrickShareFonts
import com.example.brickshare.ui.theme.HederaGreen
import com.example.brickshare.ui.theme.DeepNavy
import com.example.brickshare.ui.theme.BuildingBlocksWhite
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(navController: NavController) {
    var viewMode by remember { mutableStateOf("list") }
    var sortOption by remember { mutableStateOf("Newest") }
    var searchQuery by remember { mutableStateOf("") }

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
            containerColor = BuildingBlocksWhite
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header
                Text(
                    text = "Browse Properties",
                    style = TextStyle(
                        fontFamily = BrickShareFonts.Halcyon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = DeepNavy
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // View Mode Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(HederaGreen.copy(alpha = 0.1f)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ToggleButton(
                        text = "List",
                        isSelected = viewMode == "list",
                        onClick = { viewMode = "list" },
                        modifier = Modifier.weight(1f)
                    )
                    ToggleButton(
                        text = "Map",
                        isSelected = viewMode == "map",
                        onClick = { viewMode = "map" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search and Filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        placeholder = {
                            Text(
                                "Search properties...",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 14.sp,
                                color = DeepNavy.copy(alpha = 0.7f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Search,
                                contentDescription = "Search",
                                tint = HederaGreen
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HederaGreen,
                            unfocusedBorderColor = DeepNavy.copy(alpha = 0.3f),
                            focusedTextColor = DeepNavy,
                            unfocusedTextColor = DeepNavy,
                            cursorColor = HederaGreen
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { /* Open filter dialog */ },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(HederaGreen.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.Rounded.FilterAlt,
                            contentDescription = "Filters",
                            tint = HederaGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sort by:",
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 14.sp,
                        color = DeepNavy.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Fixed line
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        TextButton(onClick = { expanded = true }) {
                            Text(
                                text = sortOption,
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 14.sp,
                                color = HederaGreen,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                Icons.Rounded.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = HederaGreen
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            listOf("Newest", "Price: Low to High", "Price: High to Low", "ROI").forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option,
                                            fontFamily = BrickShareFonts.Halcyon,
                                            color = DeepNavy
                                        )
                                    },
                                    onClick = {
                                        sortOption = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content View
                if (viewMode == "list") {
                    PropertyListView(navController)
                } else {
                    PropertyMapView()
                }
            }
        }
    }
}

@Composable
fun ToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) HederaGreen else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else DeepNavy,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyListView(navController: NavController) {
    val properties = listOf(
        Triple("123 Main St", "$50/share", "5% ROI"),
        Triple("456 Oak Ave", "$75/share", "4.5% ROI"),
        Triple("789 Pine Blvd", "$60/share", "4.8% ROI")
    )

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(properties) { (address, price, roi) ->
            PropertyCard(
                address = address,
                pricePerShare = price,
                roi = roi,
                onClick = { navController.navigate("property_detail/1") }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyCard(
    address: String,
    pricePerShare: String,
    roi: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = DeepNavy,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        ),
        onClick = onClick
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.placeholder_property),
                contentDescription = address,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = address,
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = pricePerShare,
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 14.sp,
                        color = HederaGreen
                    )
                    Text(
                        text = roi,
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = HederaGreen
                    )
                }
            }
        }
    }
}

@Composable
fun PropertyMapView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BuildingBlocksWhite),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Map View (Placeholder)",
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 20.sp,
            color = DeepNavy.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            PropertyPin(Modifier.offset(x = 100.dp, y = 150.dp))
            PropertyPin(Modifier.offset(x = 200.dp, y = 100.dp))
            PropertyPin(Modifier.offset(x = 150.dp, y = 250.dp))
        }
    }
}

@Composable
fun PropertyPin(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(DeepNavy.copy(alpha = 0.2f))
                .offset(y = 1.dp)
        )
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(HederaGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Home,
                contentDescription = "Property",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBrowseScreen() {
    BrowseScreen(navController = rememberNavController())
}