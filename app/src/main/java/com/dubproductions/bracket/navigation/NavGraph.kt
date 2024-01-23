package com.dubproductions.bracket.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.ui.main.HomeScreen
import com.dubproductions.bracket.ui.main.ParticipatingScreen
import com.dubproductions.bracket.ui.main.SettingsScreen
import com.dubproductions.bracket.ui.main.hosting.BracketScreen
import com.dubproductions.bracket.ui.main.hosting.EditTournamentScreen
import com.dubproductions.bracket.ui.main.hosting.HostingScreen
import com.dubproductions.bracket.ui.main.hosting.TournamentCreationScreen
import com.dubproductions.bracket.ui.main.hosting.participant.ParticipantMatchesScreen
import com.dubproductions.bracket.ui.main.hosting.participant.ParticipantsScreen
import com.dubproductions.bracket.ui.onboarding.LoginScreen
import com.dubproductions.bracket.ui.onboarding.RegistrationScreen
import com.dubproductions.bracket.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch


@Composable
fun NavHost(
    navController: NavHostController,
    loggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (loggedIn) {
            Map.BottomBar.route
        } else {
            Map.PreLogin.route
        }
    ) {
        preLoginMap(navController)
        bottomBarNavMap()
    }
}

fun NavGraphBuilder.preLoginMap(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.Login.route,
        route = Map.PreLogin.route
    ) {
        loginScreen(navController)
        registrationScreen(navController)
    }
}

fun NavGraphBuilder.bottomBarNavMap() {
    navigation(
        startDestination = Map.Home.route,
        route = Map.BottomBar.route
    ) {
        homeNavMap()
        hostingNavMap()
        participatingNavMap()
        settingsNavMap()
    }
}

fun NavGraphBuilder.homeNavMap() {
    navigation(
        startDestination = Screen.Home.route,
        route = Map.Home.route
    ) {
        composable(
            route = Screen.Home.route
        ) {
            homeScreen()
        }
    }
}

fun NavGraphBuilder.hostingNavMap() {
    navigation(
        startDestination = Screen.Hosting.route,
        route = Map.Hosting.route
    ) {
        hostingScreen()
        tournamentCreationScreen()
        editTournamentScreen()
        bracketScreen()
        participantsScreen()
        participantMatchesScreen()
    }
}

fun NavGraphBuilder.participatingNavMap() {
    navigation(
        startDestination = Screen.Participating.route,
        route = Map.Participating.route
    ) {
        participantsScreen()
    }
}

fun NavGraphBuilder.settingsNavMap() {
    navigation(
        startDestination = Screen.Settings.route,
        route = Map.Settings.route
    ) {
        settingsScreen()
    }
}

fun NavGraphBuilder.loginScreen(
    navController: NavHostController
) {
    composable(
        route = Screen.Login.route
    ) {
        val onboardingViewModel: OnboardingViewModel = it.sharedViewModel(navController)
        val uiState by onboardingViewModel.loginUIState.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()

        LoginScreen(
            loginClick = { email, password ->
                onboardingViewModel.disableLoginScreenUI()
                coroutineScope.launch {
                    val result = onboardingViewModel.loginUser(email, password)
                    onboardingViewModel.enableLoginScreenUI()
                    if (result) {
                        navController.navigate(Map.BottomBar.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    } else {
                        onboardingViewModel.showLoginFailureDialog()
                    }
                }
            },
            registrationClick = { navController.navigate(Screen.Registration.route) },
            forgotPasswordClick = { email ->
                onboardingViewModel.disableLoginScreenUI()
                coroutineScope.launch {
                    val result = onboardingViewModel.resetPassword(email)
                    onboardingViewModel.enableLoginScreenUI()
                    if (result) {
                        onboardingViewModel.showPasswordResetSuccessDialog()
                    } else {
                        onboardingViewModel.showPasswordResetFailureDialog()
                    }
                }
            },
            dismissDialog = { onboardingViewModel.dismissLoginDialogs() },
            uiState = uiState
        )
    }
}

fun NavGraphBuilder.registrationScreen(
    navController: NavHostController
) {
    composable(
        route = Screen.Registration.route
    ) {
        val onboardingViewModel: OnboardingViewModel = it.sharedViewModel(navController)
        RegistrationScreen()
    }
}

fun NavGraphBuilder.homeScreen() {
    composable(
        route = Screen.Home.route
    ) {
        HomeScreen()
    }
}

fun NavGraphBuilder.hostingScreen() {
    composable(
        route = Screen.Hosting.route
    ) {
        HostingScreen()
    }
}

fun NavGraphBuilder.tournamentCreationScreen() {
    composable(
        route = Screen.TournamentCreation.route
    ) {
        TournamentCreationScreen()
    }
}

fun NavGraphBuilder.editTournamentScreen() {
    composable(
        route = Screen.EditTournament.route
    ) {
        EditTournamentScreen()
    }
}

fun NavGraphBuilder.bracketScreen() {
    composable(
        route = Screen.Bracket.route
    ) {
        BracketScreen()
    }
}

fun NavGraphBuilder.participantsScreen() {
    composable(
        route = Screen.Participants.route
    ) {
        ParticipantsScreen()
    }
}

fun NavGraphBuilder.participantMatchesScreen() {
    composable(
        route = Screen.ParticipantMatches.route
    ) {
        ParticipantMatchesScreen()
    }
}

fun NavGraphBuilder.participatingScreen() {
    composable(
        route = Screen.Participating.route
    ) {
        ParticipatingScreen()
    }
}

fun NavGraphBuilder.settingsScreen() {
    composable(
        route = Screen.Settings.route
    ) {
        SettingsScreen()
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavHostController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}

//@Composable
//fun MainNavHost(
//    mainNavController: NavHostController,
//    bottomBarNavController: NavHostController,
//    loggedIn: Boolean
//) {
//    NavHost(
//        navController = mainNavController,
//        startDestination = if (loggedIn) {
//            Screen.Home.route
//        } else {
//            Screen.Login.route
//        }
//    ) {
//        composable(
//            route = Screen.Login.route
//        ) {
//            PreLoginNavHost(
//                mainNavController = mainNavController
//            )
//        }
//
//        composable(
//            route = Screen.Home.route
//        ) {
//            BottomBarNavHost(
//                bottomBarNavController = bottomBarNavController
//            )
//        }
//    }
//}
//
//@Composable
//fun BottomBarNavHost(
//    bottomBarNavController: NavHostController,
//    userViewModel: UserViewModel = hiltViewModel()
//) {
//    NavHost(
//        navController = bottomBarNavController,
//        startDestination = Screen.Home.route,
//    ) {
//        composable(route = Screen.Home.route) {
//            HomeNavHost(userViewModel = userViewModel)
//        }
//        composable(route = Screen.Hosting.route) {
//            HostingNavHost(userViewModel = userViewModel)
//        }
//        composable(route = Screen.Participating.route) {
//            ParticipatingNavHost()
//        }
//        composable(route = Screen.Settings.route) {
//            SettingsNavHost()
//        }
//    }
//}
//
//@Composable
//fun PreLoginNavHost(
//    mainNavController: NavHostController,
//    onboardingViewModel: OnboardingViewModel = hiltViewModel()
//) {
//    val preLoginNavController = rememberNavController()
//    NavHost(
//        navController = preLoginNavController,
//        startDestination = Screen.Login.route
//    ) {
//        composable(
//            route = Screen.Login.route
//        ) {
//            LoginScreen(
//                onboardingViewModel = onboardingViewModel,
//                preLoginNavController = preLoginNavController,
//                mainNavHostController = mainNavController
//            )
//        }
//
//        composable(
//            route = Screen.Registration.route
//        ) {
//            RegistrationScreen(
//                onboardingViewModel = onboardingViewModel,
//                mainNavController = mainNavController
//            )
//        }
//    }
//}
//
//// Home tab NavGraph
//@Composable
//fun HomeNavHost(
//    userViewModel: UserViewModel
//) {
//    val homeNavController = rememberNavController()
//    NavHost(
//        navController = homeNavController,
//        startDestination = Screen.Home.route
//    ) {
//        composable(
//            route = Screen.Home.route
//        ) {
//            HomeScreen(
//                userViewModel = userViewModel
//            )
//        }
//    }
//}
//
//// Hosting tab NavGraph
//@Composable
//fun HostingNavHost(
//    userViewModel: UserViewModel,
//    participantViewModel: ParticipantViewModel = hiltViewModel()
//) {
//    val hostingNavController = rememberNavController()
//
//    NavHost(
//        navController = hostingNavController,
//        startDestination = Screen.Hosting.route
//    ) {
//        composable(
//            route = Screen.Hosting.route
//        ) {
//            HostingScreen(
//                userViewModel = userViewModel,
//                hostingNavController = hostingNavController
//            )
//        }
//
//        composable(
//            route = Screen.TournamentCreation.route
//        ) {
//            TournamentCreationScreen(
//                navController = hostingNavController
//            )
//        }
//
//        composable(
//            route = Screen.EditTournament.route
//        ) {
//            EditTournamentScreen(
//                userViewModel = userViewModel,
//                hostingNavController = hostingNavController
//            )
//        }
//
//        composable(
//            route = Screen.Participants.route
//        ) {
//            ParticipantsScreen(
//                userViewModel = userViewModel,
//                hostingNavController = hostingNavController,
//                participantViewModel = participantViewModel
//            )
//        }
//
//        composable(
//            route = Screen.Bracket.route
//        ) {
//            BracketScreen(userViewModel = userViewModel)
//        }
//
//        composable(
//            route = Screen.ParticipantMatches.route
//        ) {
//            ParticipantMatchesScreen(
//                participantViewModel = participantViewModel
//            )
//        }
//    }
//}
//
//@Composable
//fun ParticipatingNavHost() {
//    val participatingNavController = rememberNavController()
//    NavHost(
//        navController = participatingNavController,
//        startDestination = Screen.Participating.route
//    ) {
//        composable(
//            route = Screen.Participating.route
//        ) {
//            ParticipatingScreen()
//        }
//    }
//}
//
//@Composable
//fun SettingsNavHost() {
//    val settingsNavController = rememberNavController()
//    NavHost(
//        navController = settingsNavController,
//        startDestination = Screen.Settings.route
//    ) {
//        composable(
//            route = Screen.Settings.route
//        ) {
//            SettingsScreen()
//        }
//    }
//}
