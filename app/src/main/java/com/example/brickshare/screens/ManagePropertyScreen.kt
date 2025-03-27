package com.example.brickshare.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

data class PropertyInvestor(
    val name: String,
    val hederaAccountId: String,
    val shares: Int
)

data class Property(
    val id: String,
    val name: String,
    val address: String,
    val value: Int,
    val totalHbarCollected: Double,
    val investors: List<PropertyInvestor>,
    val isOwner: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePropertyScreen(
    navController: NavController,
    propertyId: String,
    userViewModel: UserViewModel
) {
    val scope = rememberCoroutineScope()
    val userId by userViewModel.userId.collectAsState()
    var property by remember { mutableStateOf<Property?>(null) }
    val db = Firebase.firestore

    // Fetch property data off the main thread, filtering by ownership
    LaunchedEffect(propertyId, userId) {
        scope.launch {
            if (userId == null) {
                println("User ID is null, skipping fetch")
                return@launch
            }

            try {
                // Fetch the property document on IO thread
                val propertyDoc = withContext(Dispatchers.IO) {
                    db.collection("properties")
                        .document(propertyId)
                        .get()
                        .await()
                }

                // Check if the document exists and user is the owner
                val ownerId = propertyDoc.getString("ownerId") ?: "Unknown"
                val isOwner = userId == ownerId

                if (!propertyDoc.exists() || !isOwner) {
                    // Property doesn't exist or user isn't the owner
                    property = Property(
                        id = propertyId,
                        name = "",
                        address = "",
                        value = 0,
                        totalHbarCollected = 0.0,
                        investors = emptyList(),
                        isOwner = false
                    )
                    println("Property $propertyId not found or not owned by $userId")
                } else {
                    // Fetch owner email if needed (optional, kept for consistency)
                    val ownerEmail = withContext(Dispatchers.IO) {
                        val ownerDoc = db.collection("users").document(ownerId).get().await()
                        ownerDoc.getString("email")?.split("@")?.get(0) ?: "Unknown"
                    }

                    property = Property(
                        id = propertyId,
                        name = propertyDoc.getString("metadata.address")?.let { "Property at $it" } ?: "Property $propertyId",
                        address = propertyDoc.getString("metadata.address") ?: "Unknown Address",
                        value = 50000, // Hardcoded for now, add to Firestore if needed
                        totalHbarCollected = propertyDoc.getDouble("totalHbarCollected") ?: 0.0,
                        investors = emptyList(), // TODO: Add investors to properties/{propertyId}
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
                        IconButton(onClick = { navController.navigate("dashboard") }) {
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
                            text = "You do not own this property",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 16.sp,
                            color = DeepNavy,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    else -> {
                        // Property Info Card
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

                        // Investors Section
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

                        // Distribution Action
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