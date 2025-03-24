package com.example.brickshare.screens

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
import com.example.brickshare.ui.theme.BrickShareFonts
import com.example.brickshare.ui.theme.HederaGreen
import com.example.brickshare.ui.theme.DeepNavy
import com.example.brickshare.ui.theme.BuildingBlocksWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

data class PropertyInfo(
    val id: String,
    val name: String,
    val pricePerShare: Int,
    val availableShares: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuySharesScreen(navController: NavController, propertyId: String) {
    val property = PropertyInfo(
        id = propertyId,
        name = "Seaside Villa",
        pricePerShare = 50,
        availableShares = 20
    )

    var shareCount by remember { mutableStateOf("5") }
    var totalAmount by remember { mutableStateOf(5 * property.pricePerShare) }
    var selectedPaymentMethod by remember { mutableStateOf("Credit Card") }
    var expanded by remember { mutableStateOf(false) }
    var isPurchaseSuccessful by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val paymentMethods = listOf("Credit Card", "Debit Card", "Bank Transfer", "PayPal")

    val checkmarkAlpha by animateFloatAsState(
        targetValue = if (showSuccess) 0.8f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

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
                // Main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Property Info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = property.name,
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
                                    text = "$${property.pricePerShare} per share",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 16.sp,
                                    color = DeepNavy.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "${property.availableShares} shares available",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 16.sp,
                                    color = HederaGreen
                                )
                            }
                        }
                    }

                    // Step Indicator
                    StepIndicator(
                        currentStep = if (isPurchaseSuccessful) 3 else if (shareCount.isNotEmpty() && shareCount.toIntOrNull() ?: 0 > 0) 2 else 1,
                        primaryColor = HederaGreen
                    )

                    // Step 1: Share Selection
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardColors(
                            containerColor = Color.White,
                            contentColor = DeepNavy,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.LightGray
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
                                    if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                                        shareCount = newValue
                                        totalAmount = (newValue.toIntOrNull() ?: 0) * property.pricePerShare
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
                                    text = "$${totalAmount}",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HederaGreen
                                )
                            }
                        }
                    }

                    // Step 2: Payment Method
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardColors(
                            containerColor = Color.White,
                            contentColor = DeepNavy,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.LightGray
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

                    Spacer(modifier = Modifier.weight(1f))

                    // Step 3: Confirm
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isPurchaseSuccessful = true
                                showSuccess = true
                                delay(2000)
                                navController.navigate("portfolio")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = shareCount.isNotEmpty() && (shareCount.toIntOrNull() ?: 0) > 0 && !isPurchaseSuccessful,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HederaGreen
                        )
                    ) {
                        Text(
                            text = "Confirm Purchase",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Success Overlay
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
                                text = "You've purchased $shareCount shares of ${property.name}",
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

@Preview(showBackground = true)
@Composable
fun PreviewBuySharesScreen() {
    BuySharesScreen(
        navController = rememberNavController(),
        propertyId = "1"
    )
}