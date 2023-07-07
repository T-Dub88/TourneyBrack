package com.dubproductions.bracket.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dubproductions.bracket.ui.onboarding.LoginScreen
import com.dubproductions.bracket.ui.onboarding.RegistrationScreen
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun SetupNavGraph(
    navHostController: NavHostController,
    userViewModel: UserViewModel = viewModel()
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Login.route
    ) {
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen(userViewModel = userViewModel, navHostController = navHostController)
        }

        composable(
            route = Screen.Registration.route
        ) {
            RegistrationScreen(userViewModel = userViewModel, navHostController = navHostController)
        }
    }
}
