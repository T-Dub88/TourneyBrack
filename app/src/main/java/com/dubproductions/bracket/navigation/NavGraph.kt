package com.dubproductions.bracket.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.Validation
import com.dubproductions.bracket.ui.LoadingScreen
import com.dubproductions.bracket.ui.main.HomeScreen
import com.dubproductions.bracket.ui.main.HostingScreen
import com.dubproductions.bracket.ui.main.ParticipatingScreen
import com.dubproductions.bracket.ui.main.SettingsScreen
import com.dubproductions.bracket.ui.onboarding.LoginScreen
import com.dubproductions.bracket.ui.onboarding.RegistrationScreen
import com.dubproductions.bracket.viewmodel.AppViewModel
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun SetupNavGraph(
    navHostController: NavHostController,
    userViewModel: UserViewModel = viewModel(),
    validation: Validation = Validation(),
    appViewModel: AppViewModel = viewModel()
) {
    // Pre Login Map
    NavHost(
        navController = navHostController,
        startDestination = Map.Onboarding.route
    ) {
        navigation(
            startDestination = Screen.Loading.route,
            route = Map.Onboarding.route
        ) {
            composable(
                route = Screen.Loading.route
            ) {
                LoadingScreen(
                    navHostController = navHostController,
                    userViewModel = userViewModel,
                    appViewModel = appViewModel
                )
            }

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
        }

        // Post Login Map
        navigation(
            startDestination = Screen.Home.route,
            route = Map.Main.route
        ) {
            composable(
                route = Screen.Home.route
            ) {
                HomeScreen(
                    userViewModel = userViewModel
                )
            }

            composable(
                route = Screen.Hosting.route
            ) {
                HostingScreen()
            }

            composable(
                route = Screen.Participating.route
            ) {
                ParticipatingScreen()
            }

            composable(
                route = Screen.Settings.route
            ) {
                SettingsScreen()
            }
        }
    }
}
