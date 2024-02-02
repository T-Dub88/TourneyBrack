package com.dubproductions.bracket.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.presentation.ui.screen.main.HomeScreen
import com.dubproductions.bracket.presentation.viewmodel.UserViewModel

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
        val userViewModel: UserViewModel = it.sharedViewModel(navController = navController)
        val user by userViewModel.user.collectAsStateWithLifecycle()
        val tournamentList by userViewModel.completedTournamentList.collectAsStateWithLifecycle()

        HomeScreen(
            username = user.username,
            tournamentList = tournamentList,
            cardPressed = { tournamentId ->
                // TODO: Navigate to info screen
            }
        )
    }
}
