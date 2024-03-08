package com.dubproductions.bracket.home.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.core.presentation.navigation.Map
import com.dubproductions.bracket.core.presentation.navigation.Screen
import com.dubproductions.bracket.core.presentation.navigation.sharedViewModel
import com.dubproductions.bracket.core.presentation.viewmodel.SharedViewModel
import com.dubproductions.bracket.home.presentation.screen.HomeScreen

fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.Home.route,
        route = Map.Home.route
    ) {
        homeScreen(navController)
    }
}

fun NavGraphBuilder.homeScreen(
    navController: NavHostController
) {
    composable(
        route = Screen.Home.route
    ) {
        val sharedViewModel: SharedViewModel = it.sharedViewModel(navController = navController)
        val user by sharedViewModel.user.collectAsStateWithLifecycle()
        val tournamentList by sharedViewModel.completedTournaments.collectAsStateWithLifecycle()

        HomeScreen(
            username = user.username,
            tournamentList = tournamentList,
            cardPressed = { tournamentId ->
                // TODO: Navigate to info screen
            }
        )
    }
}
