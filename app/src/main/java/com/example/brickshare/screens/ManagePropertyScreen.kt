package com.example.brickshare.screens

import androidx.compose.foundation.clickable
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
import com.example.brickshare.viewmodel.UserViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Property(
    val id: String,
    val name: String,
    val address: String,
    val value: Int,
    val totalHbarCollected: Double,
    val investors: List<PropertyInvestor>,
    val isOwner: Boolean
)

data class PropertyInvestor(
    val name: String,
    val hederaAccountId: String,
    val shares: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePropertyScreen(
    navController: NavController,
    propertyId: String, // Ignored for now
    userViewModel: UserViewModel
) {
    val scope = rememberCoroutineScope()
    val userId by userViewModel.userId.collectAsState()
    var properties by remember { mutableStateOf<List<Property>>(emptyList()) }
    val db = Firebase.firestore

    LaunchedEffect(userId) {
        scope.launch {
            if (userId == null) {
                println("User ID is null, skipping fetch")
                return@launch
            }

            println("Fetching properties for user: $userId")
            try {
                val propertiesSnapshot = db.collection("properties")
                    .whereEqualTo("ownerId", userId)
                    .get()
                    .await()

                properties = propertiesSnapshot.documents.mapNotNull { propDoc ->
                    val ownerId = propDoc.getString("ownerId") ?: "Unknown"
                    val isOwner = userId == ownerId
                    if (!isOwner) null // Shouldn’t happen due to query, but safety first
                    else Property(
                        id = propDoc.id,
                        name = propDoc.getString("metadata.address")?.let { "Property at $it" } ?: "Property ${propDoc.id}",
                        address = propDoc.getString("metadata.address") ?: "Unknown Address",
                        value = 50000, // Hardcoded for now
                        totalHbarCollected = propDoc.getDouble("totalHbarCollected") ?: 0.0,
                        investors = emptyList(),
                        isOwner = true
                    )
                }
                println("Fetched ${properties.size} properties for user $userId")
            } catch (e: Exception) {
                println("Error fetching properties: ${e.message}")
                properties = emptyList()
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
                            "Manage My Properties",
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
                    properties.isEmpty() && userId != null -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    properties.isEmpty() -> {
                        Text(
                            text = "You don’t own any properties yet",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 16.sp,
                            color = DeepNavy,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(properties) { property ->
                                PropertyListItem(property) {
                                    navController.navigate("manage_property/${property.id}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PropertyListItem(property: Property, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                text = property.name,
                style = TextStyle(
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = property.address,
                fontFamily = BrickShareFonts.Halcyon,
                fontSize = 14.sp,
                color = DeepNavy.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$${property.value}",
                style = TextStyle(
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HederaGreen
                )
            )
        }
    }
}