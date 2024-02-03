package com.dubproductions.bracket.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.domain.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
): ViewModel() {

    private val _appReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val appReady: StateFlow<Boolean> = _appReady.asStateFlow()

    private val _isLoggedIn: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        userLoginStatus()
    }

    fun updatedLogInStatus(status: Boolean) {
        _isLoggedIn.update {
            status
        }
    }

    private fun updateReadyStatus() {
        _appReady.update {
            true
        }
    }

    private fun userLoginStatus() {
        viewModelScope.launch {
            val loginCheckResult = onboardingRepository.checkLoginStatus()
            updatedLogInStatus(loginCheckResult)
            updateReadyStatus()
        }
    }

}
