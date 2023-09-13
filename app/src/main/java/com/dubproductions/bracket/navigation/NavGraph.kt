package com.dubproductions.bracket.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dubproductions.bracket.Validation
import com.dubproductions.bracket.ui.LoadingScreen
import com.dubproductions.bracket.ui.main.HomeScreen
import com.dubproductions.bracket.ui.onboarding.LoginScreen
import com.dubproductions.bracket.ui.onboarding.RegistrationScreen
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun SetupNavGraph(
    navHostController: NavHostController,
    userViewModel: UserViewModel = viewModel(),
    validation: Validation = Validation()
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Loading.route
    ) {
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen(
                userViewModel = userViewModel,
                navHostController = navHostController,
                validation = validation
            )
        }

        composable(
            route = Screen.Registration.route
        ) {
            RegistrationScreen(
                userViewModel = userViewModel,
                navHostController = navHostController,
                validation = validation
            )
        }

        composable(
            route = Screen.Home.route
        ) {
            HomeScreen()
        }

        composable(
            route = Screen.Loading.route
        ) {
            LoadingScreen(
                navHostController = navHostController,
                userViewModel = userViewModel
            )
        }
    }
}
