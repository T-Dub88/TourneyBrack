package com.dubproductions.bracket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
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
        return tournamentRepository.registerUser(
                email = email,
                password = password,
                username = username,
                firstName = firstName,
                lastName = lastName
            )
    }

    suspend fun loginUser(
        email: String,
        password: String
    ): Boolean {
        return tournamentRepository.signInUser(
                email = email,
                password = password
            )
    }

    suspend fun resetPassword(email: String): Boolean {
        return tournamentRepository.resetPassword(email = email)
    }

}
