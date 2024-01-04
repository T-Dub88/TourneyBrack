package com.dubproductions.bracket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.data.Tournament
import com.dubproductions.bracket.data.User
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepositoryImpl
): ViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()

    private val _completedTournamentList: MutableStateFlow<MutableList<Tournament>> = MutableStateFlow(mutableListOf())
    val completedTournamentList: StateFlow<MutableList<Tournament>> = _completedTournamentList

    private var loggedIn: Boolean = false

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
            val registrationResult = tournamentRepository.registerUser(
                email = email,
                password = password,
                username = username,
                firstName = firstName,
                lastName = lastName
            )

            if (registrationResult) {
                fetchUserData {
                    loggedIn = true
                    onComplete(it)
                }
            } else {
                onComplete(false)
            }

        }
    }

    fun loginUser(
        email: String,
        password: String,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val signInResult = tournamentRepository.signInUser(
                email = email,
                password = password
            )

            if (signInResult) {
                fetchUserData {
                    loggedIn = true
                    onComplete(it)
                }
            } else {
                onComplete(false)
            }

        }
    }

    private fun fetchUserData(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            tournamentRepository.fetchUserData { user ->
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

    suspend fun resetPassword(email: String): Boolean {
        return viewModelScope.async {
            tournamentRepository.resetPassword(email = email)
        }.await()
    }

    fun userLoggedIn(onComplete: (Boolean) -> Unit) {
        if (tournamentRepository.checkLoginStatus()) {
            fetchUserData {
                onComplete(it)
            }
        } else {
            onComplete(false)
        }
    }

}
