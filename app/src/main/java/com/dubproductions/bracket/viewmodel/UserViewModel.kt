package com.dubproductions.bracket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.data.Tournament
import com.dubproductions.bracket.data.User
import com.dubproductions.bracket.firebase.FirebaseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val firebaseManager: FirebaseManager
): ViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()

    private val _completedTournamentList: MutableStateFlow<MutableList<Tournament>> = MutableStateFlow(mutableListOf())
    val completedTournamentList: StateFlow<MutableList<Tournament>> = _completedTournamentList

    private val _hostingTournamentList: MutableStateFlow<MutableList<Tournament>> = MutableStateFlow(mutableListOf())
    val hostingTournamentList: StateFlow<MutableList<Tournament>> = _hostingTournamentList

    private var loggedIn: Boolean = false

    private fun updateHostingTournamentList(tournament: Tournament){
        _hostingTournamentList.update { previousTourneyList ->
            if (tournament !in previousTourneyList) {
                previousTourneyList.add(tournament)
            }
            previousTourneyList
        }
    }

    private fun updateCompletedTournamentList(tournament: Tournament) {
        _completedTournamentList.update { previousTourneyList ->
            if (tournament !in previousTourneyList) {
                previousTourneyList.add(tournament)
            }
            previousTourneyList
        }
    }

    private fun updateUser(updatedUser: User) {
        _user.update { updatedUser }
        Log.i("User", "updateUser: ${user.value}")
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
                if (success) {
                    fetchUserData {
                        loggedIn = true
                        onComplete(it)
                    }
                } else {
                    onComplete(false)
                }
            }
        }
    }

    fun loginUser(
        email: String,
        password: String,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            firebaseManager.signInUser(
                email = email,
                password = password
            ) { success ->
                if (success) {
                    fetchUserData {
                        loggedIn = true
                        onComplete(it)
                    }
                } else {
                    onComplete(false)
                }
            }
        }
    }

    private fun fetchUserData(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            firebaseManager.fetchUserData { user ->
                when {
                    loggedIn && user?.userId != null -> {
                        updateUser(user)
                    }
                    !loggedIn && user?.userId != null -> {
                        updateUser(user)
                        loggedIn = true
                        onComplete(true)
                    }
                    !loggedIn && user?.userId == null -> {
                        onComplete(false)
                    }
                }
            }
        }
    }

    fun resetPassword(email: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            firebaseManager.resetUserPassword(email = email) { success ->
                onComplete(success)
            }
        }
    }

    fun userLoggedIn(onComplete: (Boolean) -> Unit) {
        if (firebaseManager.checkLoginStatus()) {
            fetchUserData {
                onComplete(it)
            }
        } else {
            onComplete(false)
        }
    }

}
