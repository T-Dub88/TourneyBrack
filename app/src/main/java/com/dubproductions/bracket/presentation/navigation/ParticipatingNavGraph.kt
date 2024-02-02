package com.dubproductions.bracket.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.presentation.ui.screen.main.ParticipatingScreen

fun NavGraphBuilder.participatingNavGraph() {
    navigation(
        startDestination = Screen.Participating.route,
        route = Map.Participating.route
    ) {
        participatingScreen()
    }
}

fun NavGraphBuilder.participatingScreen() {
    composable(
        route = Screen.Participating.route
    ) {
        ParticipatingScreen()
    }
}
