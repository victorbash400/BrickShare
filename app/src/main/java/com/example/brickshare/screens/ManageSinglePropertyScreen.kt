package com.example.brickshare.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.brickshare.viewmodel.UserViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSinglePropertyScreen(
    navController: NavController,
    propertyId: String,
    userViewModel: UserViewModel
) {
    val scope = rememberCoroutineScope()
    val userId by userViewModel.userId.collectAsState()
    var property by remember { mutableStateOf<Property?>(null) }
    val db = Firebase.firestore

    LaunchedEffect(propertyId, userId) {
        scope.launch {
            if (userId == null) {
                println("User ID is null, skipping fetch")
                return@launch
            }

            println("Fetching property with ID: $propertyId for user: $userId")
            try {
                val propertyDoc = withContext(Dispatchers.IO) {
                    db.collection("properties")
                        .document(propertyId)
                        .get()
                        .await()
                }

                println("Property Doc Data: ${propertyDoc.data}")

                val ownerId = propertyDoc.getString("ownerId") ?: "Unknown"
                val isOwner = userId == ownerId
                println("User ID: $userId, Owner ID: $ownerId, Is Owner: $isOwner")

                if (!propertyDoc.exists()) {
                    property = Property(
                        id = propertyId,
                        name = "Not Found",
                        address = "Property does not exist",
                        value = 0,
                        totalHbarCollected = 0.0,
                        investors = emptyList(),
                        isOwner = false
                    )
                    println("Property $propertyId not found in Firestore")
                } else if (!isOwner) {
                    property = Property(
                        id = propertyId,
                        name = "Access Denied",
                        address = "You do not own this property",
                        value = 0,
                        totalHbarCollected = 0.0,
                        investors = emptyList(),
                        isOwner = false
                    )
                    println("Property $propertyId not owned by $userId")
                } else {
                    property = Property(
                        id = propertyId,
                        name = propertyDoc.getString("metadata.address")?.let { "Property at $it" } ?: "Property $propertyId",
                        address = propertyDoc.getString("metadata.address") ?: "Unknown Address",
                        value = 50000,
                        totalHbarCollected = propertyDoc.getDouble("totalHbarCollected") ?: 0.0,
                        investors = emptyList(),
                        isOwner = true
                    )
                    println("Property $propertyId fetched successfully for owner $userId")
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
                            "Manage Property",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("manage_property") }) { // Back to list
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
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                when {
                    userId == null -> {
                        Text(
                            text = "Please sign in to manage properties",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 16.sp,
                            color = DeepNavy,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    property == null -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    !property!!.isOwner -> {
                        Text(
                            text = property!!.address,
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 16.sp,
                            color = DeepNavy,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    else -> {
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
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = property!!.name,
                                    style = TextStyle(
                                        fontFamily = BrickShareFonts.Halcyon,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepNavy
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = property!!.address,
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 16.sp,
                                    color = DeepNavy.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "$${property!!.value}",
                                    style = TextStyle(
                                        fontFamily = BrickShareFonts.Halcyon,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HederaGreen
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                LinearProgressIndicator(
                                    progress = 1f,
                                    modifier = Modifier.fillMaxWidth(),
                                    color = HederaGreen,
                                    trackColor = DeepNavy.copy(alpha = 0.2f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Fully Funded",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 14.sp,
                                    color = HederaGreen,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Text(
                            text = "Investors",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardColors(
                                containerColor = Color.White,
                                contentColor = DeepNavy,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.LightGray
                            )
                        ) {
                            Text(
                                text = "Investors not available (TODO: Add to Firestore)",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 16.sp,
                                color = DeepNavy.copy(alpha = 0.7f),
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardColors(
                                containerColor = Color.White,
                                contentColor = DeepNavy,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.LightGray
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Ready to distribute profits?",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "You have ${property!!.totalHbarCollected} HBAR available to distribute",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 14.sp,
                                    color = DeepNavy.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { /* TODO: Call transferToOwner and distributeHbar later */ },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = HederaGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Distribute ${property!!.totalHbarCollected} HBAR",
                                        fontFamily = BrickShareFonts.Halcyon,
                                        fontSize = 16.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}