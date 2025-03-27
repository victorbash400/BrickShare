package com.example.brickshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.brickshare.ui.theme.BrickShareTheme
import com.example.brickshare.viewmodel.UserViewModel
import androidx.lifecycle.lifecycleScope
import com.example.brickshare.navigation.Navigation
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hedera.hashgraph.sdk.PrivateKey
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val userViewModel = UserViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("851529847458-u9ei43n2hmnth6ge71bf112368ikvctc.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        // Enable edge-to-edge display
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
        lifecycleScope.cancel() // Clean up coroutines when Activity is destroyed
        Log.d("MainActivity", "Activity destroyed, coroutine scope cancelled")
    }

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d("GoogleSignIn", "Google sign-in successful: ${account.email}")
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Google sign-in failed: ${e.statusCode}", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { authResult ->
            if (authResult.isSuccessful) {
                val user = auth.currentUser
                Log.d("GoogleSignIn", "Signed in: ${user?.email}")

                user?.uid?.let { uid ->
                    userViewModel.setUserId(uid) // Store user ID in ViewModel
                }

                lifecycleScope.launch {
                    try {
                        val db = Firebase.firestore
                        user?.uid?.let { uid ->
                            val userDocRef = db.collection("users").document(uid)
                            val userDoc = userDocRef.get().await()

                            if (!userDoc.exists()) {
                                Log.d("Firestore", "User does not exist, creating document")
                                val userData = mapOf(
                                    "uid" to uid,
                                    "email" to user.email,
                                    "role" to "investor",
                                    "createdAt" to System.currentTimeMillis()
                                )
                                userDocRef.set(userData).await()
                                Log.d("Firestore", "User document created successfully")

                                val (hederaAccountId, hederaPrivateKey) = HederaUtils.createHederaAccount()
                                val hederaData = mapOf(
                                    "hederaAccountId" to hederaAccountId,
                                    "hederaPrivateKey" to hederaPrivateKey,
                                    "walletPublicKey" to PrivateKey.fromString(hederaPrivateKey).publicKey.toString(),
                                    "hbarBalance" to 5
                                )
                                userDocRef.update(hederaData).await()
                                Log.d("Firestore", "Hedera account added: $hederaAccountId with private key: $hederaPrivateKey")
                            } else {
                                val existingAccountId = userDoc.getString("hederaAccountId")
                                if (existingAccountId == null || userDoc.getString("hederaPrivateKey") == null) {
                                    Log.d("Firestore", "User exists but missing Hedera account, creating one")
                                    val (hederaAccountId, hederaPrivateKey) = HederaUtils.createHederaAccount()
                                    val hederaData = mapOf(
                                        "hederaAccountId" to hederaAccountId,
                                        "hederaPrivateKey" to hederaPrivateKey,
                                        "walletPublicKey" to PrivateKey.fromString(hederaPrivateKey).publicKey.toString(),
                                        "hbarBalance" to 5
                                    )
                                    userDocRef.update(hederaData).await()
                                    Log.d("Firestore", "Hedera account added: $hederaAccountId with private key: $hederaPrivateKey")
                                } else {
                                    Log.d("Firestore", "User already exists with Hedera account: $existingAccountId")
                                }
                            }

                            val role = userDoc.getString("role") ?: "investor"
                            userViewModel.setUserRole(role) // Store role in ViewModel
                        }
                    } catch (e: Exception) {
                        Log.e("Hedera/Firestore", "Error: ${e.message}", e)
                    }
                }
            } else {
                Log.e("GoogleSignIn", "Sign-in failed: ${authResult.exception?.message}", authResult.exception)
            }
        }
    }
}
