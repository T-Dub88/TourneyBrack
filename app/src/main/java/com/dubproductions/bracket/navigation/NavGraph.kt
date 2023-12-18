package com.dubproductions.bracket.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
fun MainNavHost(
    mainNavController: NavHostController,
    userViewModel: UserViewModel,
    appViewModel: AppViewModel
) {
    NavHost(
        navController = mainNavController,
        startDestination = Screen.Loading.route,
    ) {
        composable(route = Screen.Loading.route) {
            PreLoginNavHost(
                userViewModel = userViewModel,
                appViewModel = appViewModel,
                mainNavHostController = mainNavController
            )
        }
        composable(route = Screen.Home.route) {
            HomeNavHost(userViewModel = userViewModel)
        }
        composable(route = Screen.Hosting.route) {
            HostingNavHost()
        }
        composable(route = Screen.Participating.route) {
            ParticipatingNavHost()
        }
        composable(route = Screen.Settings.route) {
            SettingsNavHost()
        }
    }
}

@Composable
fun PreLoginNavHost(
    userViewModel: UserViewModel,
    appViewModel: AppViewModel,
    mainNavHostController: NavHostController
) {
    val preLoginNavController = rememberNavController()
    NavHost(
        navController = preLoginNavController,
        startDestination = Screen.Loading.route
    ) {
        composable(
            route = Screen.Loading.route
        ) {
            LoadingScreen(
                mainNavController = mainNavHostController,
                preLoginNavHostController = preLoginNavController,
                userViewModel = userViewModel,
                appViewModel = appViewModel
            )
        }

        composable(
            route = Screen.Login.route
        ) {
            LoginScreen(
                userViewModel = userViewModel,
                preLoginNavHostController = preLoginNavController,
                mainNavHostController = mainNavHostController
            )
        }

        composable(
            route = Screen.Registration.route
        ) {
            RegistrationScreen(
                userViewModel = userViewModel,
                mainNavHostController = mainNavHostController
            )
        }
    }
}

// Home tab NavGraph
@Composable
fun HomeNavHost(
    userViewModel: UserViewModel
) {
    val homeNavController = rememberNavController()
    NavHost(
        navController = homeNavController,
        startDestination = Screen.Home.route
    ) {
        composable(
            route = Screen.Home.route
        ) {
            HomeScreen(
                userViewModel = userViewModel
            )
        }
    }
}

// Hosting tab NavGraph
@Composable
fun HostingNavHost(

) {
    val hostingNavController = rememberNavController()
    NavHost(
        navController = hostingNavController,
        startDestination = Screen.Hosting.route
    ) {
        composable(
            route = Screen.Hosting.route
        ) {
            HostingScreen()
        }
    }
}

@Composable
fun ParticipatingNavHost(

) {
    val participatingNavController = rememberNavController()
    NavHost(
        navController = participatingNavController,
        startDestination = Screen.Participating.route
    ) {
        composable(
            route = Screen.Participating.route
        ) {
            ParticipatingScreen()
        }
    }
}

@Composable
fun SettingsNavHost(

) {
    val settingsNavController = rememberNavController()
    NavHost(
        navController = settingsNavController,
        startDestination = Screen.Settings.route
    ) {
        composable(
            route = Screen.Settings.route
        ) {
            SettingsScreen()
        }
    }
}
