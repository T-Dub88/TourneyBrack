package com.dubproductions.bracket.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dubproductions.bracket.ui.main.HomeScreen
import com.dubproductions.bracket.ui.main.ParticipatingScreen
import com.dubproductions.bracket.ui.main.SettingsScreen
import com.dubproductions.bracket.ui.main.hosting.BracketScreen
import com.dubproductions.bracket.ui.main.hosting.EditTournamentScreen
import com.dubproductions.bracket.ui.main.hosting.HostingScreen
import com.dubproductions.bracket.ui.main.hosting.ParticipantsScreen
import com.dubproductions.bracket.ui.main.hosting.TournamentCreationScreen
import com.dubproductions.bracket.ui.onboarding.LoginScreen
import com.dubproductions.bracket.ui.onboarding.RegistrationScreen
import com.dubproductions.bracket.viewmodel.OnboardingViewModel
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun MainNavHost(
    mainNavController: NavHostController,
    bottomBarNavController: NavHostController,
    loggedIn: Boolean
) {
    NavHost(
        navController = mainNavController,
        startDestination = if (loggedIn) {
            Screen.Home.route
        } else {
            Screen.Login.route
        }
    ) {
        composable(
            route = Screen.Login.route
        ) {
            PreLoginNavHost(
                mainNavController = mainNavController
            )
        }

        composable(
            route = Screen.Home.route
        ) {
            BottomBarNavHost(
                bottomBarNavController = bottomBarNavController
            )
        }
    }
}

@Composable
fun BottomBarNavHost(
    bottomBarNavController: NavHostController,
    userViewModel: UserViewModel = hiltViewModel()
) {
    NavHost(
        navController = bottomBarNavController,
        startDestination = Screen.Home.route,
    ) {
        composable(route = Screen.Home.route) {
            HomeNavHost(userViewModel = userViewModel)
        }
        composable(route = Screen.Hosting.route) {
            HostingNavHost(userViewModel = userViewModel)
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
    mainNavController: NavHostController,
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    val preLoginNavController = rememberNavController()
    NavHost(
        navController = preLoginNavController,
        startDestination = Screen.Login.route
    ) {
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen(
                onboardingViewModel = onboardingViewModel,
                preLoginNavController = preLoginNavController,
                mainNavHostController = mainNavController
            )
        }

        composable(
            route = Screen.Registration.route
        ) {
            RegistrationScreen(
                onboardingViewModel = onboardingViewModel,
                mainNavController = mainNavController
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
    userViewModel: UserViewModel
) {
    val hostingNavController = rememberNavController()
    NavHost(
        navController = hostingNavController,
        startDestination = Screen.Hosting.route
    ) {
        composable(
            route = Screen.Hosting.route
        ) {
            HostingScreen(
                userViewModel = userViewModel,
                hostingNavController = hostingNavController
            )
        }

        composable(
            route = Screen.TournamentCreation.route
        ) {
            TournamentCreationScreen(
                navController = hostingNavController
            )
        }

        composable(
            route = Screen.EditTournament.route
        ) {
            EditTournamentScreen(
                userViewModel = userViewModel,
                hostingNavController = hostingNavController
            )
        }

        composable(
            route = Screen.Participants.route
        ) {
            ParticipantsScreen(userViewModel = userViewModel)
        }

        composable(
            route = Screen.Bracket.route
        ) {
            BracketScreen(userViewModel = userViewModel)
        }
    }
}

@Composable
fun ParticipatingNavHost() {
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
fun SettingsNavHost() {
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
