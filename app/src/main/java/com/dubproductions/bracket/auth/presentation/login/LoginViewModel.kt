package com.dubproductions.bracket.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.auth.domain.repository.OnboardingRepository
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
) : ViewModel() {

    private val _loginState: MutableStateFlow<LoginState> = MutableStateFlow(
        LoginState()
    )
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private fun updateLoginUIState(newState: LoginState) {
        _loginState.update {
            newState
        }
    }

    fun dismissLoginDialogs() {
        val newState = loginState.value.copy(
            showPasswordResetSuccessDialog = false,
            showLoginFailureDialog = false,
            showPasswordResetFailureDialog = false
        )
        updateLoginUIState(newState)
    }

    fun showLoginFailureDialog() {
        val newState = loginState.value.copy(showLoginFailureDialog = true)
        updateLoginUIState(newState)
    }

    private fun showPasswordResetSuccessDialog() {
        val newState = loginState.value.copy(showPasswordResetSuccessDialog = true)
        updateLoginUIState(newState)
    }

    private fun showPasswordResetFailureDialog() {
        val newState = loginState.value.copy(showPasswordResetFailureDialog = true)
        updateLoginUIState(newState)
    }

    private fun disableLoginScreenUI() {
        val newState = loginState.value.copy(enable = false)
        updateLoginUIState(newState)
    }

    private fun enableLoginScreenUI() {
        val newState = loginState.value.copy(enable = true)
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
            enableLoginScreenUI()
        }
    }

}
