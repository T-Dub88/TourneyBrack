package com.dubproductions.bracket.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.data.User
import com.dubproductions.bracket.firebase.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {

    private val firebaseManager: FirebaseManager = FirebaseManager()

    private val _user: MutableStateFlow<User> = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()

    fun updateUser(user: User) {
        _user.update { user }
    }

    fun registerUser(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            firebaseManager.registerUser(
                email = email,
                password = password,
                username = username,
                firstName = firstName,
                lastName = lastName
            ) { success: Boolean ->
                onComplete(success)
            }
        }
    }

}
