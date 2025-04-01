package com.example.brickshare.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brickshare.ui.theme.BrickShareFonts
import com.example.brickshare.ui.theme.HederaGreen
import com.example.brickshare.ui.theme.DeepNavy
import com.example.brickshare.ui.theme.BuildingBlocksWhite
import com.example.brickshare.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val userId by userViewModel.userId.collectAsState()
    val userRole by userViewModel.userRole.collectAsState()
    val scope = rememberCoroutineScope()
    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    // State for user data
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var hederaAccountId by remember { mutableStateOf("") }
    var hederaPrivateKey by remember { mutableStateOf("") }
    var hederaPublicKey by remember { mutableStateOf("") }
    var hbarBalance by remember { mutableStateOf(0.0) }
    var showPrivateKey by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Fetch user data from Firestore and Firebase Auth
    LaunchedEffect(userId) {
        if (userId != null) {
            scope.launch {
                try {
                    isLoading = true
                    val userDoc = db.collection("users").document(userId!!).get().await()
                    hederaAccountId = userDoc.getString("hederaAccountId") ?: ""
                    hederaPrivateKey = userDoc.getString("hederaPrivateKey") ?: ""
                    hederaPublicKey = userDoc.getString("walletPublicKey") ?: ""
                    hbarBalance = userDoc.getDouble("hbarBalance") ?: 0.0

                    val currentUser = auth.currentUser
                    displayName = currentUser?.displayName ?: "Unknown User"
                    email = currentUser?.email ?: "No email"
                } catch (e: Exception) {
                    println("Error fetching profile data: ${e.message}")
                    displayName = "Error"
                    email = "Failed to load"
                    hederaAccountId = "N/A"
                    hederaPublicKey = "N/A"
                    hbarBalance = 0.0
                } finally {
                    isLoading = false
                }
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
                            "Profile",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = DeepNavy
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BuildingBlocksWhite)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    userId == null -> {
                        Text(
                            "Please sign in to view your profile",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 16.sp,
                            color = DeepNavy
                        )
                    }
                    isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    else -> {
                        // Profile Section
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardColors(
                                containerColor = Color.White,
                                contentColor = DeepNavy,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.LightGray
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(HederaGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Person,
                                        contentDescription = "Profile Avatar",
                                        modifier = Modifier.size(40.dp),
                                        tint = HederaGreen
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = displayName,
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy
                                )
                                Text(
                                    text = email,
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 16.sp,
                                    color = DeepNavy.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = userRole ?: "Investor",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 14.sp,
                                    color = HederaGreen
                                )
                            }
                        }

                        // Wallet Section
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
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
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Hedera Wallet",
                                        fontFamily = BrickShareFonts.Halcyon,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepNavy
                                    )
                                    IconButton(onClick = {
                                        scope.launch {
                                            isLoading = true
                                            // Simulate balance refresh (replace with real Hedera API call in HederaUtils)
                                            try {
                                                val userDoc = db.collection("users").document(userId!!).get().await()
                                                hbarBalance = userDoc.getDouble("hbarBalance") ?: 0.0
                                            } catch (e: Exception) {
                                                println("Error refreshing balance: ${e.message}")
                                            } finally {
                                                isLoading = false
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Refresh,
                                            contentDescription = "Refresh Balance",
                                            tint = HederaGreen
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                // Account ID
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Account ID",
                                            fontFamily = BrickShareFonts.Halcyon,
                                            fontSize = 14.sp,
                                            color = DeepNavy.copy(alpha = 0.7f)
                                        )
                                        Text(
                                            hederaAccountId,
                                            fontFamily = BrickShareFonts.Halcyon,
                                            fontSize = 12.sp,
                                            color = DeepNavy,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    IconButton(onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Hedera Account ID", hederaAccountId)
                                        clipboard.setPrimaryClip(clip)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Rounded.ContentCopy,
                                            contentDescription = "Copy Account ID",
                                            tint = HederaGreen
                                        )
                                    }
                                }
                                // Public Key
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Public Key",
                                            fontFamily = BrickShareFonts.Halcyon,
                                            fontSize = 14.sp,
                                            color = DeepNavy.copy(alpha = 0.7f)
                                        )
                                        Text(
                                            hederaPublicKey,
                                            fontFamily = BrickShareFonts.Halcyon,
                                            fontSize = 12.sp,
                                            color = DeepNavy,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    IconButton(onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Hedera Public Key", hederaPublicKey)
                                        clipboard.setPrimaryClip(clip)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Rounded.ContentCopy,
                                            contentDescription = "Copy Public Key",
                                            tint = HederaGreen
                                        )
                                    }
                                }
                                // Private Key
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Private Key",
                                            fontFamily = BrickShareFonts.Halcyon,
                                            fontSize = 14.sp,
                                            color = DeepNavy.copy(alpha = 0.7f)
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                if (showPrivateKey) hederaPrivateKey else "${hederaPrivateKey.take(6)}...${hederaPrivateKey.takeLast(4)}",
                                                fontFamily = BrickShareFonts.Halcyon,
                                                fontSize = 12.sp,
                                                color = DeepNavy,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            TextButton(onClick = { showPrivateKey = !showPrivateKey }) {
                                                Text(
                                                    if (showPrivateKey) "Hide" else "Show",
                                                    color = HederaGreen,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                    IconButton(onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Hedera Private Key", hederaPrivateKey)
                                        clipboard.setPrimaryClip(clip)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Rounded.ContentCopy,
                                            contentDescription = "Copy Private Key",
                                            tint = HederaGreen
                                        )
                                    }
                                }
                                // HBAR Balance
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Balance: $hbarBalance HBAR",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HederaGreen
                                )
                            }
                        }

                        // Logout Button
                        Button(
                            onClick = {
                                auth.signOut()
                                userViewModel.setUserId(null)
                                userViewModel.setUserRole(null)
                                navController.navigate("welcome") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Logout",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}