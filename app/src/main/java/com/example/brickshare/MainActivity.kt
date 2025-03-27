package com.example.brickshare

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.brickshare.ui.theme.BrickShareTheme
import com.example.brickshare.navigation.Navigation
import com.example.brickshare.viewmodel.UserViewModel
import com.hedera.hashgraph.sdk.PrivateKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { authResult ->
                if (authResult.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    Log.d("GoogleSignIn", "Signed in: ${user?.email}")

                    // Handle Firestore and Hedera operations in a coroutine
                    coroutineScope.launch {
                        try {
                            val db = Firebase.firestore
                            user?.uid?.let { uid ->
                                val userDocRef = db.collection("users").document(uid)
                                val userDoc = userDocRef.get().await()

                                if (!userDoc.exists()) {
                                    Log.d("Firestore", "User does not exist, creating document")
                                    val userData = mapOf(
                                        "uid" to uid,
                                        "email" to user?.email,
                                        "role" to "investor",
                                        "createdAt" to System.currentTimeMillis()
                                    )
                                    userDocRef.set(userData).await()
                                    Log.d("Firestore", "User document created successfully")

                                    Log.d("Firestore", "Now attempting Hedera account creation")
                                    val (hederaAccountId, hederaPrivateKey) = HederaUtils.createHederaAccount()
                                    val hederaData = mapOf(
                                        "hederaAccountId" to hederaAccountId,
                                        "walletPublicKey" to PrivateKey.fromString(hederaPrivateKey).publicKey.toString(),
                                        "hbarBalance" to 5
                                    )
                                    userDocRef.update(hederaData).await()
                                    Log.d("Firestore", "Hedera account added: $hederaAccountId")
                                } else {
                                    val existingAccountId = userDoc.getString("hederaAccountId")
                                    if (existingAccountId == null) {
                                        Log.d("Firestore", "User exists but has no Hedera account, creating one")
                                        val (hederaAccountId, hederaPrivateKey) = HederaUtils.createHederaAccount()
                                        val hederaData = mapOf(
                                            "hederaAccountId" to hederaAccountId,
                                            "walletPublicKey" to PrivateKey.fromString(hederaPrivateKey).publicKey.toString(),
                                            "hbarBalance" to 5
                                        )
                                        userDocRef.update(hederaData).await()
                                        Log.d("Firestore", "Hedera account added: $hederaAccountId")
                                    } else {
                                        Log.d("Firestore", "User already exists with Hedera account: $existingAccountId")
                                    }
                                }
                            } ?: Log.w("GoogleSignIn", "User UID is null")
                        } catch (e: Exception) {
                            Log.e("Hedera/Firestore", "Error: ${e.javaClass.name} - ${e.message}", e)
                        }
                    }
                } else {
                    Log.e("GoogleSignIn", "Sign-in failed: ${authResult.exception?.message}", authResult.exception)
                }
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Sign-in error: ${e.statusCode} - ${e.message}", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("851529847458-u9ei43n2hmnth6ge71bf112368ikvctc.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Set up Compose UI
        setContent {
            BrickShareTheme(darkTheme = true) {
                Navigation(
                    userViewModel = userViewModel,
                    signInLauncher = signInLauncher,
                    googleSignInClient = googleSignInClient
                )
            }
        }

        // Initialize Hedera client eagerly to catch any setup issues early
        try {
            HederaClient.client // Trigger lazy initialization
        } catch (e: Exception) {
            Log.e("MainActivity", "Hedera client initialization failed on startup: ${e.message}", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Clean up coroutines when Activity is destroyed
        Log.d("MainActivity", "Activity destroyed, coroutine scope cancelled")
    }
}