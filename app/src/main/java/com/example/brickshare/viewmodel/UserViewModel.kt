package com.example.brickshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    init {
        // Initialize with current Firebase user and fetch role from Firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            _userId.value = currentUser.uid
            fetchUserRole(currentUser.uid)
        }

        // Listen to auth state changes to keep userId updated
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val uid = auth.currentUser?.uid
            if (uid != _userId.value) {
                _userId.value = uid
                if (uid != null) {
                    fetchUserRole(uid)
                } else {
                    _userRole.value = null // Clear role if signed out
                }
            }
        }
    }

    private fun fetchUserRole(uid: String) {
        viewModelScope.launch {
            try {
                val userDoc = Firebase.firestore.collection("users").document(uid).get().await()
                _userRole.value = userDoc.getString("role") ?: "investor" // Default to "investor" if null
                println("UserViewModel: Fetched role for $uid: ${_userRole.value}")
            } catch (e: Exception) {
                println("UserViewModel: Error fetching role for $uid: ${e.message}")
                _userRole.value = "investor" // Fallback on error
            }
        }
    }

    fun setUserId(uid: String?) {
        _userId.value = uid
        if (uid != null && _userRole.value == null) {
            fetchUserRole(uid) // Fetch role if not already set
        }
    }

    fun setUserRole(role: String?) {
        _userRole.value = role
    }

    override fun onCleared() {
        super.onCleared()
        // Note: Auth listener persists across ViewModel lifecycle; no need to remove here
    }
}