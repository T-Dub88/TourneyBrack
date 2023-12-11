package com.dubproductions.bracket.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
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
    validation: Validation = Validation(),
    controlBottomBarVisibility: (Boolean) -> Unit
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
                controlBottomBarVisibility(Screen.Loading.showBottomBar)
                LoadingScreen(
                    navHostController = navHostController,
                    userViewModel = userViewModel
                )
            }

            composable(
                route = Screen.Login.route
            ) {
                controlBottomBarVisibility(Screen.Login.showBottomBar)
                LoginScreen(
                    userViewModel = userViewModel,
                    navHostController = navHostController,
                    validation = validation
                )
            }

            composable(
                route = Screen.Registration.route
            ) {
                controlBottomBarVisibility(Screen.Registration.showBottomBar)
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
                controlBottomBarVisibility(Screen.Home.showBottomBar)
                HomeScreen(
                    userViewModel = userViewModel
                )
            }

            composable(
                route = Screen.Hosting.route
            ) {
                controlBottomBarVisibility(Screen.Hosting.showBottomBar)
                // Todo: Make screen
                Log.i("Nav Graph", "Hosting Screen")
            }

            composable(
                route = Screen.Participating.route
            ) {
                controlBottomBarVisibility(Screen.Participating.showBottomBar)
                // Todo: Make screen
                Log.i("Nav Graph", "Participating Screen")
            }

            composable(
                route = Screen.Settings.route
            ) {
                controlBottomBarVisibility(Screen.Settings.showBottomBar)
                // Todo: Make screen
                Log.i("Nav Graph", "Settings Screen")
            }
        }
    }
}
