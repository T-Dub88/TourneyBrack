package com.dubproductions.bracket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import com.dubproductions.bracket.ui_state.LoginUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepositoryImpl
): ViewModel() {

    private val _loginUIState: MutableStateFlow<LoginUIState> = MutableStateFlow(
        LoginUIState()
    )
    val loginUIState: StateFlow<LoginUIState> = _loginUIState.asStateFlow()

    init {
        Log.i("LoginViewModel", "Created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("LoginViewModel", "Cleared")
    }

    private fun updateLoginUIState(newState: LoginUIState) {
        _loginUIState.update {
            newState
        }
    }

    fun dismissLoginDialogs() {
        val newState = loginUIState.value.copy(
            showPasswordResetSuccessDialog = false,
            showLoginFailureDialog = false,
            showPasswordResetFailureDialog = false
        )
        updateLoginUIState(newState)
    }

    fun showLoginFailureDialog() {
        val newState = loginUIState.value.copy(showLoginFailureDialog = true)
        updateLoginUIState(newState)
    }

    fun showPasswordResetSuccessDialog() {
        val newState = loginUIState.value.copy(showPasswordResetSuccessDialog = true)
        updateLoginUIState(newState)
    }

    fun showPasswordResetFailureDialog() {
        val newState = loginUIState.value.copy(showPasswordResetFailureDialog = true)
        updateLoginUIState(newState)
    }

    fun disableLoginScreenUI() {
        val newState = loginUIState.value.copy(enable = false)
        updateLoginUIState(newState)
    }

    fun enableLoginScreenUI() {
        val newState = loginUIState.value.copy(enable = true)
        updateLoginUIState(newState)
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
