package com.dubproductions.bracket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepositoryImpl
): ViewModel() {

    init {
        Log.i("OnboardingViewModel", "Created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("OnboardingViewModel", "Cleared")
    }

    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String
    ): Boolean {
        return viewModelScope.async {
            tournamentRepository.registerUser(
                email = email,
                password = password,
                username = username,
                firstName = firstName,
                lastName = lastName
            )
        }.await()
    }

    suspend fun loginUser(
        email: String,
        password: String
    ): Boolean {
        return viewModelScope.async {
            tournamentRepository.signInUser(
                email = email,
                password = password
            )
        }.await()
    }

    suspend fun resetPassword(email: String): Boolean {
        return viewModelScope.async {
            tournamentRepository.resetPassword(email = email)
        }.await()
    }

}
