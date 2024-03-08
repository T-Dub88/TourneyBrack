package com.dubproductions.bracket.participating.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.core.presentation.navigation.Map
import com.dubproductions.bracket.core.presentation.navigation.Screen
import com.dubproductions.bracket.participating.presentation.participating.ParticipatingScreen

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
