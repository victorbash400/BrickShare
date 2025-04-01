package com.example.brickshare.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun BrowseScreen(navController: NavController) {
    var viewMode by remember { mutableStateOf("list") }
    var sortOption by remember { mutableStateOf("Newest") }
    var searchQuery by remember { mutableStateOf("") }
    val db = Firebase.firestore
    var properties by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch properties from Firestore
    LaunchedEffect(sortOption, searchQuery) {
        coroutineScope.launch {
            val query = db.collection("properties")
                .let { q ->
                    if (searchQuery.isNotEmpty()) {
                        q.whereGreaterThanOrEqualTo("metadata.address", searchQuery)
                            .whereLessThanOrEqualTo("metadata.address", "$searchQuery\uf8ff")
                    } else {
                        q // No filtering when search is empty, fetch all properties
                    }
                }
            when (sortOption) {
                "Price: Low to High" -> query.orderBy("pricePerToken")
                "Price: High to Low" -> query.orderBy("pricePerToken", com.google.firebase.firestore.Query.Direction.DESCENDING)
                "Newest" -> query.orderBy("propertyId", com.google.firebase.firestore.Query.Direction.DESCENDING)
                else -> query
            }.get()
                .addOnSuccessListener { result ->
                    properties = result.documents.mapNotNull { it.data }
                }
                .addOnFailureListener { e ->
                    println("Error fetching properties: ${e.message}")
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
            containerColor = BuildingBlocksWhite
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp), // Removed shadow for cleaner look
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
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        Icons.Rounded.Clear,
                                        contentDescription = "Clear",
                                        tint = DeepNavy
                                    )
                                }
                            }
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
                        onClick = { /* Open filter dialog later if needed */ },
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
                    Spacer(modifier = Modifier.width(8.dp))
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
                            listOf("Newest", "Price: Low to High", "Price: High to Low").forEach { option ->
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

                AnimatedContent(
                    targetState = viewMode,
                    transitionSpec = { fadeIn() with fadeOut() }
                ) { mode ->
                    if (mode == "list") {
                        PropertyListView(navController, properties)
                    } else {
                        PropertyMapView(properties)
                    }
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
fun PropertyListView(navController: NavController, properties: List<Map<String, Any>>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(properties) { property ->
            val address = (property["metadata"] as? Map<*, *>)?.get("address") as? String ?: "Unknown"
            val pricePerShare = "${property["pricePerToken"] ?: 0} HBAR/share"
            val roi = "N/A" // ROI not included as before
            PropertyCard(
                address = address,
                pricePerShare = pricePerShare,
                roi = roi,
                onClick = { navController.navigate("buy_shares/${property["propertyId"]}") }
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
fun PropertyMapView(properties: List<Map<String, Any>>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BuildingBlocksWhite), // Clean white background
        contentAlignment = Alignment.Center
    ) {
        // Mock map background with subtle gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color.White, HederaGreen.copy(alpha = 0.05f)),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                    )
                )
        )

        // Property pins
        properties.forEachIndexed { index, property ->
            val offsetX = (index * 50 + 50).dp
            val offsetY = (index * 70 + 100).dp
            PropertyPin(
                modifier = Modifier.offset(x = offsetX, y = offsetY),
                label = (property["metadata"] as? Map<*, *>)?.get("address") as? String ?: "Property $index"
            )
        }

        // Zoom controls (mock)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = { /* Zoom in */ }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Rounded.Add, contentDescription = "Zoom In", tint = DeepNavy)
            }
            IconButton(onClick = { /* Zoom out */ }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Rounded.Remove, contentDescription = "Zoom Out", tint = DeepNavy)
            }
        }

        // Map placeholder text
        Text(
            text = "Map View (Preview)",
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 16.sp,
            color = DeepNavy.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
        )
    }
}

@Composable
fun PropertyPin(modifier: Modifier = Modifier, label: String) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(HederaGreen.copy(alpha = 0.9f))
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.Home,
                contentDescription = "Property",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = label,
                fontFamily = BrickShareFonts.Halcyon,
                fontSize = 12.sp,
                color = DeepNavy,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}