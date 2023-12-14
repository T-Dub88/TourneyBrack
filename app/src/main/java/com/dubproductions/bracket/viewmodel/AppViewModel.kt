package com.dubproductions.bracket.viewmodel

import androidx.lifecycle.ViewModel
import com.dubproductions.bracket.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AppViewModel: ViewModel() {

    private val _currentNavState: MutableStateFlow<Screen> = MutableStateFlow(Screen.Loading)
    val currentNavState: StateFlow<Screen> = _currentNavState

    fun updateNavState(screen: Screen) {
        _currentNavState.update { screen }
    }

}
