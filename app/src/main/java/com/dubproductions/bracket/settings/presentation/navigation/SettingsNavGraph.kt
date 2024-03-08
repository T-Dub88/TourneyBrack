package com.dubproductions.bracket.settings.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.core.presentation.navigation.Map
import com.dubproductions.bracket.core.presentation.navigation.Screen
import com.dubproductions.bracket.settings.presentation.settings.SettingsScreen

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
