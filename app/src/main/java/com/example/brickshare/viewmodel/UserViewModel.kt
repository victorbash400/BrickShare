package com.example.brickshare.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    // Use String for simplicity; later, convert to your UserRole enum if needed
    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    fun setUserRole(role: String) {
        _userRole.value = role
    }
}