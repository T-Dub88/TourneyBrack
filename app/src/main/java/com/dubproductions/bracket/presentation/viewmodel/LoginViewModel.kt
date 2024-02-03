package com.dubproductions.bracket.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.domain.repository.OnboardingRepository
import com.dubproductions.bracket.presentation.ui.state.LoginUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
): ViewModel() {

    private val _loginUIState: MutableStateFlow<LoginUIState> = MutableStateFlow(
        LoginUIState()
    )
    val loginUIState: StateFlow<LoginUIState> = _loginUIState.asStateFlow()

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

    private fun showPasswordResetSuccessDialog() {
        val newState = loginUIState.value.copy(showPasswordResetSuccessDialog = true)
        updateLoginUIState(newState)
    }

    private fun showPasswordResetFailureDialog() {
        val newState = loginUIState.value.copy(showPasswordResetFailureDialog = true)
        updateLoginUIState(newState)
    }

    private fun disableLoginScreenUI() {
        val newState = loginUIState.value.copy(enable = false)
        updateLoginUIState(newState)
    }

    private fun enableLoginScreenUI() {
        val newState = loginUIState.value.copy(enable = true)
        updateLoginUIState(newState)
    }

    suspend fun loginUser(
        email: String,
        password: String
    ): Boolean {
        disableLoginScreenUI()
        val signInResult =  onboardingRepository.signInUser(
                email = email,
                password = password
        )
        enableLoginScreenUI()
        return signInResult

    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            disableLoginScreenUI()
            val resetResult = onboardingRepository.resetPassword(email)
            if (resetResult) {
                showPasswordResetSuccessDialog()
            } else {
                showPasswordResetFailureDialog()
            }
        }
    }

}
