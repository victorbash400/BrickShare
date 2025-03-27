package com.example.brickshare.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    fun setUserId(uid: String?) {  // Changed to String?
        _userId.value = uid
    }

    fun setUserRole(role: String?) {  // Changed to String?
        _userRole.value = role
    }
}