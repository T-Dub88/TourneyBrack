package com.dubproductions.bracket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import com.dubproductions.bracket.ui_state.RegistrationScreenUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepositoryImpl
): ViewModel() {

    private val _uiState: MutableStateFlow<RegistrationScreenUIState> = MutableStateFlow(
        RegistrationScreenUIState()
    )
    val uiState: StateFlow<RegistrationScreenUIState> = _uiState.asStateFlow()

    init {
        Log.i("RegistrationViewModel", "Created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("RegistrationViewModel", "Destroyed")
    }

    private fun updateUIState(newUIState: RegistrationScreenUIState) {
        _uiState.update {
            newUIState
        }
    }

    fun showDialog() {
        val newUIState = uiState.value.copy(
            displayRegistrationFailureDialog = true
        )
        updateUIState(newUIState)
    }

    fun hideDialog() {
        val newUIState = uiState.value.copy(
            displayRegistrationFailureDialog = false
        )
        updateUIState(newUIState)
    }

    fun disableUI() {
        val newUIState = uiState.value.copy(
            uiEnabled = false
        )
        updateUIState(newUIState)
    }

    fun enableUI() {
        val newUIState = uiState.value.copy(
            uiEnabled = true
        )
        updateUIState(newUIState)
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

}
