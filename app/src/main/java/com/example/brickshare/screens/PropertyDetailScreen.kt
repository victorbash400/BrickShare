package com.example.brickshare.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brickshare.R
import com.example.brickshare.ui.theme.BrickShareFonts
import com.example.brickshare.ui.theme.HederaGreen
import com.example.brickshare.ui.theme.DeepNavy
import com.example.brickshare.ui.theme.BuildingBlocksWhite
import com.example.brickshare.viewmodel.UserViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    navController: NavController,
    propertyId: String,
    userViewModel: UserViewModel // Added to check ownership
) {
    val scope = rememberCoroutineScope()
    val userId by userViewModel.userId.collectAsState()
    val userRole by userViewModel.userRole.collectAsState()
    var property by remember { mutableStateOf<Property?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    val db = Firebase.firestore

    LaunchedEffect(propertyId, userId) {
        scope.launch {
            println("Fetching property with ID: $propertyId for user: $userId")
            try {
                val propertyDoc = withContext(Dispatchers.IO) {
                    db.collection("properties")
                        .document(propertyId)
                        .get()
                        .await()
                }

                println("Property Doc Data: ${propertyDoc.data}")

                if (propertyDoc.exists()) {
                    val ownerId = propertyDoc.getString("ownerId") ?: "Unknown"
                    property = Property(
                        id = propertyId,
                        name = propertyDoc.getString("metadata.address")?.let { "Property at $it" } ?: "Property $propertyId",
                        address = propertyDoc.getString("metadata.address") ?: "Unknown Address",
                        value = 50000, // Hardcoded for now, fetch from Firestore if added
                        totalHbarCollected = propertyDoc.getDouble("totalHbarCollected") ?: 0.0,
                        investors = emptyList(),
                        isOwner = userId == ownerId
                    )
                    description = propertyDoc.getString("metadata.description") ?: "No description available"
                    println("Property $propertyId fetched successfully")
                } else {
                    property = Property(
                        id = propertyId,
                        name = "Not Found",
                        address = "Property does not exist",
                        value = 0,
                        totalHbarCollected = 0.0,
                        investors = emptyList(),
                        isOwner = false
                    )
                    description = "Property not found"
                    println("Property $propertyId not found in Firestore")
                }
            } catch (e: Exception) {
                println("Error fetching property $propertyId: ${e.message}")
                property = Property(
                    id = propertyId,
                    name = "Error",
                    address = "Failed to load",
                    value = 0,
                    totalHbarCollected = 0.0,
                    investors = emptyList(),
                    isOwner = false
                )
                description = "Error loading property"
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
                if (property == null) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
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
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }

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
                        Text(
                            property!!.name,
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

                        // Map Preview (placeholder)
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
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
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
                                    property!!.address,
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 12.sp,
                                    color = BuildingBlocksWhite
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Description Section
                        Text(
                            "Description",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (property!!.isOwner && isEditing) {
                            TextField(
                                value = description,
                                onValueChange = { description = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                textStyle = TextStyle(
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 16.sp,
                                    color = DeepNavy
                                ),
                                label = { Text("Property Description") },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedIndicatorColor = HederaGreen,
                                    unfocusedIndicatorColor = DeepNavy.copy(alpha = 0.5f)
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        try {
                                            db.collection("properties")
                                                .document(propertyId)
                                                .update("metadata.description", description)
                                                .await()
                                            println("Description updated for $propertyId")
                                            isEditing = false
                                        } catch (e: Exception) {
                                            println("Error updating description: ${e.message}")
                                        }
                                    }
                                },
                                modifier = Modifier.align(Alignment.End),
                                colors = ButtonDefaults.buttonColors(containerColor = HederaGreen)
                            ) {
                                Text(
                                    "Save",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                        } else {
                            Text(
                                description,
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 16.sp,
                                color = DeepNavy.copy(alpha = 0.8f)
                            )
                            if (property!!.isOwner) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { isEditing = true },
                                    modifier = Modifier.align(Alignment.End),
                                    colors = ButtonDefaults.buttonColors(containerColor = HederaGreen)
                                ) {
                                    Text(
                                        "Edit Description",
                                        fontFamily = BrickShareFonts.Halcyon,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        PropertyFeaturesList()

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action Button
                        if (!property!!.isOwner) {
                            Button(
                                onClick = { navController.navigate("buy_shares/$propertyId") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HederaGreen)
                            ) {
                                Text(
                                    "Buy Shares",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        } else {
                            Button(
                                onClick = { navController.navigate("manage_property/$propertyId") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HederaGreen)
                            ) {
                                Text(
                                    "Manage Property",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
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
        PropertyFeatureItem("Size", "120 m²") // Hardcoded for now, fetch later if needed
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