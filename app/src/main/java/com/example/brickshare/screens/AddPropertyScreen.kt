package com.example.brickshare.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(navController: NavController) {
    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 4

    val address = remember { mutableStateOf("") }
    val size = remember { mutableStateOf("") }
    val valuation = remember { mutableStateOf("") }
    val sharePrice = remember { mutableStateOf("") }
    val totalShares = remember { mutableStateOf("") }
    val uploadedPhotos = remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val db = Firebase.firestore

    // Validation for enabling Next/Submit button
    val isStepValid = when (currentStep) {
        1 -> address.value.isNotBlank() && size.value.toIntOrNull() != null
        2 -> uploadedPhotos.value > 0
        3 -> valuation.value.toDoubleOrNull() != null && sharePrice.value.toDoubleOrNull() != null && totalShares.value.toIntOrNull() != null
        4 -> true // Review step is always valid
        else -> false
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
                            "Add New Property",
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                LinearProgressIndicator(
                    progress = currentStep.toFloat() / totalSteps,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = HederaGreen,
                    trackColor = DeepNavy.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Step $currentStep of $totalSteps",
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 16.sp,
                    color = HederaGreen
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    when (currentStep) {
                        1 -> "Property Details"
                        2 -> "Upload Photos"
                        3 -> "Financial Information"
                        else -> "Review & Submit"
                    },
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )

                Spacer(modifier = Modifier.height(24.dp))

                when (currentStep) {
                    1 -> PropertyDetailsStep(address, size)
                    2 -> UploadPhotosStep(uploadedPhotos)
                    3 -> FinancialInfoStep(valuation, sharePrice, totalShares)
                    4 -> ReviewStep(address.value, size.value, valuation.value, sharePrice.value, totalShares.value, uploadedPhotos.value)
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = BrickShareFonts.Halcyon,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, DeepNavy.copy(alpha = 0.3f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = DeepNavy
                            )
                        ) {
                            Text(
                                "Previous",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Button(
                        onClick = {
                            if (currentStep < totalSteps) {
                                if (isStepValid) currentStep++
                            } else {
                                isLoading = true
                                errorMessage = null
                                coroutineScope.launch {
                                    try {
                                        val ownerId = FirebaseAuth.getInstance().currentUser?.uid
                                            ?: throw IllegalStateException("User not authenticated")

                                        val propertyId = db.collection("properties").document().id
                                        val tokenId = HederaUtils.createPropertyToken(propertyId, totalShares.value.toInt())

                                        val propertyData = hashMapOf(
                                            "propertyId" to propertyId,
                                            "ownerId" to ownerId,
                                            "tokenId" to tokenId,
                                            "totalTokens" to totalShares.value.toInt(),
                                            "pricePerToken" to sharePrice.value.toDouble(),
                                            "rentalIncomePool" to 0.0,
                                            "metadata" to hashMapOf(
                                                "address" to address.value,
                                                "squareFootage" to size.value.toInt(),
                                                "valuationReport" to "Valuation: $${valuation.value}"
                                            ),
                                            "transactionHistory" to listOf("Created by $ownerId")
                                        )

                                        db.collection("properties").document(propertyId)
                                            .set(propertyData)
                                            .await()

                                        navController.navigate("dashboard")
                                    } catch (e: Exception) {
                                        Log.e("AddProperty", "Error: ${e.message}", e)
                                        errorMessage = "Failed to add property: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HederaGreen
                        ),
                        enabled = !isLoading && isStepValid
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                if (currentStep < totalSteps) "Next" else "Submit",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailsStep(address: MutableState<String>, size: MutableState<String>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Enter the property details below",
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 16.sp,
            color = DeepNavy.copy(alpha = 0.7f)
        )

        OutlinedTextField(
            value = address.value,
            onValueChange = { address.value = it },
            label = { Text("Property Address", fontFamily = BrickShareFonts.Halcyon) },
            placeholder = { Text("e.g., 123 Main St, Copenhagen", fontFamily = BrickShareFonts.Halcyon) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = HederaGreen) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HederaGreen,
                unfocusedBorderColor = DeepNavy.copy(alpha = 0.3f),
                focusedTextColor = DeepNavy,
                unfocusedTextColor = DeepNavy,
                cursorColor = HederaGreen
            )
        )

        OutlinedTextField(
            value = size.value,
            onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) size.value = it },
            label = { Text("Property Size (sqft)", fontFamily = BrickShareFonts.Halcyon) },
            placeholder = { Text("e.g., 2000", fontFamily = BrickShareFonts.Halcyon) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.SquareFoot, contentDescription = null, tint = HederaGreen) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HederaGreen,
                unfocusedBorderColor = DeepNavy.copy(alpha = 0.3f),
                focusedTextColor = DeepNavy,
                unfocusedTextColor = DeepNavy,
                cursorColor = HederaGreen
            )
        )
    }
}

@Composable
fun UploadPhotosStep(uploadedPhotos: MutableState<Int>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Upload high-quality photos of your property",
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 16.sp,
            color = DeepNavy.copy(alpha = 0.7f)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(HederaGreen.copy(alpha = 0.1f))
                .border(
                    width = 2.dp,
                    color = HederaGreen.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { uploadedPhotos.value++ },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.AddPhotoAlternate,
                    contentDescription = "Upload Main Photo",
                    modifier = Modifier.size(48.dp),
                    tint = HederaGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Upload Main Photo",
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 16.sp,
                    color = HederaGreen
                )
                Text(
                    "Tap to browse files",
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 12.sp,
                    color = DeepNavy.copy(alpha = 0.7f)
                )
            }
        }

        if (uploadedPhotos.value > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${uploadedPhotos.value} photos uploaded",
                fontFamily = BrickShareFonts.Halcyon,
                fontSize = 14.sp,
                color = HederaGreen
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialInfoStep(
    valuation: MutableState<String>,
    sharePrice: MutableState<String>,
    totalShares: MutableState<String>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Enter the financial details for your property",
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 16.sp,
            color = DeepNavy.copy(alpha = 0.7f)
        )

        OutlinedTextField(
            value = valuation.value,
            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) valuation.value = it },
            label = { Text("Property Valuation ($)", fontFamily = BrickShareFonts.Halcyon) },
            placeholder = { Text("e.g., 100000", fontFamily = BrickShareFonts.Halcyon) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = HederaGreen) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HederaGreen,
                unfocusedBorderColor = DeepNavy.copy(alpha = 0.3f),
                focusedTextColor = DeepNavy,
                unfocusedTextColor = DeepNavy,
                cursorColor = HederaGreen
            )
        )

        OutlinedTextField(
            value = sharePrice.value,
            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) sharePrice.value = it },
            label = { Text("Share Price ($)", fontFamily = BrickShareFonts.Halcyon) },
            placeholder = { Text("e.g., 50", fontFamily = BrickShareFonts.Halcyon) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Paid, contentDescription = null, tint = HederaGreen) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HederaGreen,
                unfocusedBorderColor = DeepNavy.copy(alpha = 0.3f),
                focusedTextColor = DeepNavy,
                unfocusedTextColor = DeepNavy,
                cursorColor = HederaGreen
            )
        )

        OutlinedTextField(
            value = totalShares.value,
            onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) totalShares.value = it },
            label = { Text("Total Shares", fontFamily = BrickShareFonts.Halcyon) },
            placeholder = { Text("e.g., 100", fontFamily = BrickShareFonts.Halcyon) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Group, contentDescription = null, tint = HederaGreen) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HederaGreen,
                unfocusedBorderColor = DeepNavy.copy(alpha = 0.3f),
                focusedTextColor = DeepNavy,
                unfocusedTextColor = DeepNavy,
                cursorColor = HederaGreen
            )
        )
    }
}

@Composable
fun ReviewStep(
    address: String,
    size: String,
    valuation: String,
    sharePrice: String,
    totalShares: String,
    photoCount: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Review Your Property Information",
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 16.sp,
            color = DeepNavy.copy(alpha = 0.7f)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ReviewItem("Property Address", address.ifEmpty { "Not provided" }, Icons.Default.LocationOn)
                ReviewItem("Property Size", if (size.isNotEmpty()) "$size sqft" else "Not provided", Icons.Default.SquareFoot)
                ReviewItem("Photo Uploads", "$photoCount photos", Icons.Default.PhotoLibrary)
                ReviewItem("Property Valuation", if (valuation.isNotEmpty()) "$$valuation" else "Not provided", Icons.Default.AttachMoney)
                ReviewItem("Share Price", if (sharePrice.isNotEmpty()) "$$sharePrice per share" else "Not provided", Icons.Default.Paid)
                ReviewItem("Total Shares", if (totalShares.isNotEmpty()) totalShares else "Not provided", Icons.Default.Group)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "By submitting this property, you agree to our terms and conditions for property listings.",
            fontFamily = BrickShareFonts.Halcyon,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = DeepNavy.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ReviewItem(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = HederaGreen, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontFamily = BrickShareFonts.Halcyon, fontSize = 14.sp, color = DeepNavy.copy(alpha = 0.7f))
            Text(value, fontFamily = BrickShareFonts.Halcyon, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
        }
    }
}