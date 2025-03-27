package com.example.brickshare.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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

    // State for user data, initialized as non-null with empty defaults
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var hederaAccountId by remember { mutableStateOf("") }
    var hederaPrivateKey by remember { mutableStateOf("") }
    var hederaPublicKey by remember { mutableStateOf("") }
    var kycVerified by remember { mutableStateOf(false) }  // New field for dynamic KYC
    var notificationsEnabled by remember { mutableStateOf(true) }

    // Fetch user data from Firestore and Google Auth
    LaunchedEffect(userId) {
        if (userId != null) {
            scope.launch {
                try {
                    // Fetch Firestore user data
                    val userDoc = db.collection("users").document(userId!!).get().await()
                    hederaAccountId = userDoc.getString("hederaAccountId") ?: ""
                    hederaPrivateKey = userDoc.getString("hederaPrivateKey") ?: ""
                    hederaPublicKey = userDoc.getString("walletPublicKey") ?: ""
                    kycVerified = userDoc.getBoolean("kycVerified") ?: false  // Assumes a Firestore field

                    // Fetch Google Auth data
                    val currentUser = auth.currentUser
                    displayName = currentUser?.displayName ?: "Unknown User"
                    email = currentUser?.email ?: "No email"
                } catch (e: Exception) {
                    println("Error fetching profile data: ${e.message}")
                    displayName = "Error"
                    email = "Failed to load"
                    hederaAccountId = "N/A"
                    hederaPublicKey = "N/A"
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
                            color = DeepNavy,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    displayName.isEmpty() || email.isEmpty() -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    else -> {
                        // Profile Section
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
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

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Account ID: ${hederaAccountId.take(6)}...${hederaAccountId.takeLast(4)}",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 14.sp,
                                    color = DeepNavy.copy(alpha = 0.6f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // KYC Badge (Dynamic)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardColors(
                                containerColor = Color.White,
                                contentColor = DeepNavy,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.LightGray
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (kycVerified) HederaGreen.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Verified,
                                        contentDescription = "KYC Status",
                                        tint = if (kycVerified) HederaGreen else Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = if (kycVerified) "Verified" else "Not Verified",
                                        fontFamily = BrickShareFonts.Halcyon,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepNavy
                                    )
                                    Text(
                                        text = if (kycVerified) "KYC verification complete" else "Complete KYC to verify",
                                        fontFamily = BrickShareFonts.Halcyon,
                                        fontSize = 14.sp,
                                        color = DeepNavy.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                        // Settings
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
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
                                Text(
                                    "Settings",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Notifications,
                                            contentDescription = "Notifications",
                                            tint = DeepNavy.copy(alpha = 0.7f)
                                        )

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Text(
                                            "Notifications",
                                            fontFamily = BrickShareFonts.Halcyon,
                                            fontSize = 16.sp,
                                            color = DeepNavy
                                        )
                                    }

                                    Switch(
                                        checked = notificationsEnabled,
                                        onCheckedChange = { notificationsEnabled = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = HederaGreen,
                                            checkedTrackColor = HederaGreen.copy(alpha = 0.5f),
                                            uncheckedThumbColor = Color.Gray,
                                            uncheckedTrackColor = DeepNavy.copy(alpha = 0.2f)
                                        )
                                    )
                                }
                            }
                        }

                        // Wallet
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
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
                                Text(
                                    "Wallet",
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ThumbUp,
                                        contentDescription = "Wallet",
                                        tint = DeepNavy.copy(alpha = 0.7f)
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            "Hedera Wallet",
                                            fontFamily = BrickShareFonts.Halcyon,
                                            fontSize = 16.sp,
                                            color = DeepNavy
                                        )

                                        Text(
                                            text = "${hederaAccountId.take(6)}...${hederaAccountId.takeLast(4)}",
                                            fontFamily = BrickShareFonts.Halcyon,
                                            fontSize = 14.sp,
                                            color = DeepNavy.copy(alpha = 0.6f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = "Public: ${hederaPublicKey.take(6)}...${hederaPublicKey.takeLast(4)}",
                                            fontFamily = BrickShareFonts.Halcyon,
                                            fontSize = 12.sp,
                                            color = DeepNavy.copy(alpha = 0.5f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    IconButton(onClick = { /* TODO: Copy address */ }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Email,
                                            contentDescription = "Copy Address",
                                            tint = HederaGreen
                                        )
                                    }
                                }
                            }
                        }

                        // Support & Logout
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable { /* Navigate to help */ },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Help",
                                        tint = DeepNavy.copy(alpha = 0.7f)
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Text(
                                        "Help & Support",
                                        fontFamily = BrickShareFonts.Halcyon,
                                        fontSize = 16.sp,
                                        color = DeepNavy
                                    )

                                    Spacer(modifier = Modifier.weight(1f))

                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "Go to Help",
                                        tint = DeepNavy.copy(alpha = 0.5f)
                                    )
                                }

                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = DeepNavy.copy(alpha = 0.2f))

                                Button(
                                    onClick = {
                                        auth.signOut()
                                        userViewModel.setUserId(null)
                                        userViewModel.setUserRole(null)
                                        navController.navigate("welcome") {
                                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
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
    }
}