package com.dubproductions.bracket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepositoryImpl
): ViewModel() {

    private val _appReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val appReady: StateFlow<Boolean> = _appReady.asStateFlow()

    private val _isLoggedIn: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        Log.i("CreationViewModel", "Created")
        userLoginStatus()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("CreationViewModel", "Cleared")
    }

    private fun updatedLogInStatus(status: Boolean) {
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
        val loggedIn = tournamentRepository.checkLoginStatus()
        if (loggedIn) {
            updatedLogInStatus(true)
            updateReadyStatus()
        } else {
            updatedLogInStatus(false)
            updateReadyStatus()
        }
    }

}