package com.dubproductions.bracket.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.ui.main.ParticipatingScreen

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
