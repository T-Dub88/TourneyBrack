package com.dubproductions.bracket.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.presentation.ui.screen.main.SettingsScreen

fun NavGraphBuilder.settingsNavGraph() {
    navigation(
        startDestination = Screen.Settings.route,
        route = Map.Settings.route
    ) {
        settingsScreen()
    }
}

fun NavGraphBuilder.settingsScreen() {
    composable(
        route = Screen.Settings.route
    ) {
        SettingsScreen()
    }
}
