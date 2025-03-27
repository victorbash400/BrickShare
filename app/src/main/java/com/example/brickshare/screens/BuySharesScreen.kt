package com.example.brickshare.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brickshare.HederaUtils
import com.example.brickshare.ui.theme.BrickShareFonts
import com.example.brickshare.ui.theme.HederaGreen
import com.example.brickshare.ui.theme.DeepNavy
import com.example.brickshare.ui.theme.BuildingBlocksWhite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuySharesScreen(navController: NavController, propertyId: String) {
    val db = Firebase.firestore
    var property by remember { mutableStateOf<Map<String, Any>?>(null) }
    var shareCount by remember { mutableStateOf("") }
    var totalAmount by remember { mutableStateOf(0.0) }
    var selectedPaymentMethod by remember { mutableStateOf("Hedera HBAR") }
    var expanded by remember { mutableStateOf(false) }
    var isPurchaseSuccessful by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val paymentMethods = listOf("Hedera HBAR")

    val checkmarkAlpha by animateFloatAsState(
        targetValue = if (showSuccess) 0.8f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

    // Fetch property data and refresh after purchase
    fun fetchPropertyData() {
        coroutineScope.launch {
            Log.d("BuyShares", "Fetching property data for ID: $propertyId")
            db.collection("properties").document(propertyId).get()
                .addOnSuccessListener { document ->
                    property = document.data
                    Log.d("BuyShares", "Property data fetched: ${document.data}")
                }
                .addOnFailureListener { e ->
                    Log.e("BuyShares", "Failed to fetch property data: ${e.message}", e)
                    errorMessage = "Failed to load property data"
                }
        }
    }

    LaunchedEffect(propertyId) {
        fetchPropertyData()
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
                            "Buy Shares",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("portfolio") }) {
                            Icon(
                                Icons.Default.ArrowBack,
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                            contentColor = DeepNavy
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = property?.get("metadata.address") as? String ?: "Loading...",
                                style = TextStyle(
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$${property?.get("pricePerToken") ?: 0} per share",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 16.sp,
                                    color = DeepNavy.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "${property?.get("totalTokens") ?: 0} shares available",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 16.sp,
                                    color = HederaGreen
                                )
                            }
                        }
                    }

                    StepIndicator(
                        currentStep = if (isPurchaseSuccessful) 3 else if (shareCount.isNotEmpty() && shareCount.toIntOrNull() ?: 0 > 0) 2 else 1,
                        primaryColor = HederaGreen
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                            contentColor = DeepNavy
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Step 1: Select Shares",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = shareCount,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || (newValue.toIntOrNull() != null)) {
                                        shareCount = newValue
                                        val shares = newValue.toIntOrNull() ?: 0
                                        val pricePerToken = (property?.get("pricePerToken") as? Number)?.toDouble() ?: 0.0
                                        totalAmount = shares * pricePerToken
                                    }
                                },
                                label = { Text("Number of Shares", fontFamily = BrickShareFonts.Halcyon) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = HederaGreen,
                                    unfocusedBorderColor = DeepNavy.copy(alpha = 0.3f),
                                    focusedTextColor = DeepNavy,
                                    unfocusedTextColor = DeepNavy,
                                    cursorColor = HederaGreen
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Cost:",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 18.sp,
                                    color = DeepNavy
                                )
                                Text(
                                    text = "$$totalAmount",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HederaGreen
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                            contentColor = DeepNavy
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Step 2: Payment Method",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = selectedPaymentMethod,
                                    onValueChange = { },
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        IconButton(onClick = { expanded = true }) {
                                            Icon(
                                                Icons.Default.ArrowDropDown,
                                                contentDescription = "Dropdown",
                                                tint = DeepNavy
                                            )
                                        }
                                    },
                                    label = { Text("Payment Method", fontFamily = BrickShareFonts.Halcyon) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = HederaGreen,
                                        unfocusedBorderColor = DeepNavy.copy(alpha = 0.3f),
                                        focusedTextColor = DeepNavy,
                                        unfocusedTextColor = DeepNavy,
                                        cursorColor = HederaGreen
                                    )
                                )

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier
                                        .width(IntrinsicSize.Max)
                                        .background(Color.White)
                                ) {
                                    paymentMethods.forEach { method ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    method,
                                                    fontFamily = BrickShareFonts.Halcyon,
                                                    color = DeepNavy
                                                )
                                            },
                                            onClick = {
                                                selectedPaymentMethod = method
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            isLoading = true
                            errorMessage = null
                            coroutineScope.launch {
                                try {
                                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                                        ?: throw IllegalStateException("User not authenticated")
                                    Log.d("BuyShares", "User authenticated: $userId")

                                    val userDoc = db.collection("users").document(userId).get().await()
                                    val buyerAccountId = userDoc.getString("hederaAccountId")
                                        ?: throw IllegalStateException("No Hedera account ID found for user")
                                    val buyerPrivateKey = userDoc.getString("hederaPrivateKey")
                                        ?: throw IllegalStateException("No Hedera private key found for user")
                                    Log.d("BuyShares", "Buyer Hedera account ID: $buyerAccountId")

                                    val tokenId = property?.get("tokenId") as? String
                                        ?: throw IllegalStateException("No token ID found for property")
                                    val amount = shareCount.toIntOrNull()
                                        ?: throw IllegalStateException("Invalid share count")
                                    val pricePerToken = (property?.get("pricePerToken") as? Number)?.toDouble()
                                        ?: throw IllegalStateException("No price per token found")

                                    Log.d("BuyShares", "Initiating purchase: $amount shares of token $tokenId for ${amount * pricePerToken} HBAR")
                                    HederaUtils.purchasePropertyTokens(
                                        tokenId = tokenId,
                                        buyerAccountId = buyerAccountId,
                                        buyerUserId = userId,
                                        amount = amount,
                                        pricePerToken = pricePerToken,
                                        buyerPrivateKey = buyerPrivateKey
                                    )
                                    Log.d("BuyShares", "Hedera token transfer successful")

                                    // Update user token balance
                                    val currentBalances = userDoc.get("tokenBalances") as? Map<String, Long> ?: emptyMap()
                                    val newBalance = (currentBalances[propertyId]?.toInt() ?: 0) + amount
                                    db.collection("users").document(userId)
                                        .update("tokenBalances.$propertyId", newBalance)
                                        .await()
                                    Log.d("BuyShares", "User token balance updated: $newBalance")

                                    // Update property transaction history and totalTokens
                                    val currentTotalTokens = (property?.get("totalTokens") as? Number)?.toInt() ?: 0
                                    val newTotalTokens = currentTotalTokens - amount
                                    db.collection("properties").document(propertyId)
                                        .update(
                                            mapOf(
                                                "transactionHistory" to FieldValue.arrayUnion("Bought $amount shares by $userId for ${amount * pricePerToken} HBAR"),
                                                "totalTokens" to newTotalTokens
                                            )
                                        )
                                        .await()
                                    Log.d("BuyShares", "Property updated: transaction history added, totalTokens now $newTotalTokens")

                                    // Refresh property data
                                    fetchPropertyData()

                                    isPurchaseSuccessful = true
                                    showSuccess = true
                                    delay(2000)
                                    navController.navigate("portfolio")
                                } catch (e: Exception) {
                                    Log.e("BuyShares", "Error: ${e.message}", e)
                                    errorMessage = when {
                                        e.message?.contains("INVALID_SIGNATURE") == true -> "Invalid signature; check your Hedera key"
                                        e.message?.contains("INSUFFICIENT_TOKEN_BALANCE") == true -> "Insufficient token balance"
                                        e.message?.contains("TOKEN_NOT_ASSOCIATED_TO_ACCOUNT") == true -> "Token not associated with your account"
                                        e.message?.contains("INSUFFICIENT_PAYER_BALANCE") == true -> "Insufficient HBAR balance"
                                        e.message?.contains("PERMISSION_DENIED") == true -> "Insufficient permissions to update Firestore"
                                        else -> "Purchase failed: ${e.message}"
                                    }
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoading && shareCount.isNotEmpty() && (shareCount.toIntOrNull() ?: 0) > 0 && !isPurchaseSuccessful && property != null,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HederaGreen
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "Confirm Purchase",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showSuccess,
                    enter = fadeIn(animationSpec = tween(300)) + scaleIn(animationSpec = tween(500)),
                    exit = fadeOut(animationSpec = tween(500)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DeepNavy.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(HederaGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Success",
                                    tint = HederaGreen,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .alpha(checkmarkAlpha)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Purchase Successful!",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = BuildingBlocksWhite
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "You've purchased $shareCount shares",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 16.sp,
                                color = BuildingBlocksWhite,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int, primaryColor: Color) {
    val totalSteps = 3
    val lineColor = primaryColor.copy(alpha = 0.3f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (step in 1..totalSteps) {
            if (step > 1) {
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                ) {
                    drawLine(
                        color = if (step <= currentStep) primaryColor else lineColor,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (step <= currentStep) primaryColor else Color.Transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = if (step <= currentStep) primaryColor else lineColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = step.toString(),
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (step <= currentStep) Color.White else DeepNavy
                )
            }
        }
    }
}