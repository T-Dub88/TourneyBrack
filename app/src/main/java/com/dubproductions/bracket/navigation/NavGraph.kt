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
import com.dubproductions.bracket.data.TournamentStatus
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
import com.dubproductions.bracket.viewmodel.EditTournamentScreenViewModel
import com.dubproductions.bracket.viewmodel.LoginViewModel
import com.dubproductions.bracket.viewmodel.ParticipantsScreenViewModel
import com.dubproductions.bracket.viewmodel.RegistrationViewModel
import com.dubproductions.bracket.viewmodel.UserViewModel
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
        preLoginMap(
            navigateToHomeScreen = {
                navController.navigate(Map.BottomBar.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                }
            },
            navigateToRegistrationScreen = {
                navController.navigate(Screen.Registration.route)
            }
        )
        bottomBarNavMap(navController)
    }
}

fun NavGraphBuilder.preLoginMap(
    navigateToHomeScreen: () -> Unit,
    navigateToRegistrationScreen: () -> Unit
) {
    navigation(
        startDestination = Screen.Login.route,
        route = Map.PreLogin.route
    ) {
        loginScreen(
            navigateToRegistrationScreen = navigateToRegistrationScreen,
            navigateToHomeScreen = navigateToHomeScreen
        )
        registrationScreen(navigateToHomeScreen)
    }
}

fun NavGraphBuilder.bottomBarNavMap(navController: NavHostController) {
    navigation(
        startDestination = Map.Home.route,
        route = Map.BottomBar.route
    ) {
        homeNavMap(navController)
        hostingNavMap(navController)
        participatingNavMap(navController)
        settingsNavMap()
    }
}

fun NavGraphBuilder.homeNavMap(navController: NavHostController) {
    navigation(
        startDestination = Screen.Home.route,
        route = Map.Home.route
    ) {
        homeScreen(navController)
    }
}

fun NavGraphBuilder.hostingNavMap(navController: NavHostController) {
    navigation(
        startDestination = Screen.Hosting.route,
        route = Map.Hosting.route
    ) {
        hostingScreen(navController)
        tournamentCreationScreen()
        editTournamentScreen(navController)
        bracketScreen()
        participantsScreen(navController)
        participantMatchesScreen()
    }
}

fun NavGraphBuilder.participatingNavMap(navController: NavHostController) {
    navigation(
        startDestination = Screen.Participating.route,
        route = Map.Participating.route
    ) {
        participantsScreen(navController)
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
    navigateToHomeScreen: () -> Unit,
    navigateToRegistrationScreen: () -> Unit
) {
    composable(
        route = Screen.Login.route
    ) {
        val loginViewModel: LoginViewModel = hiltViewModel()
        val uiState by loginViewModel.loginUIState.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()

        LoginScreen(
            loginClick = { email, password ->
                loginViewModel.disableLoginScreenUI()
                coroutineScope.launch {
                    val result = loginViewModel.loginUser(email, password)
                    loginViewModel.enableLoginScreenUI()
                    if (result) {
                        navigateToHomeScreen()
                    } else {
                        loginViewModel.showLoginFailureDialog()
                    }
                }
            },
            registrationClick = { navigateToRegistrationScreen() },
            forgotPasswordClick = { email ->
                loginViewModel.disableLoginScreenUI()
                coroutineScope.launch {
                    val result = loginViewModel.resetPassword(email)
                    loginViewModel.enableLoginScreenUI()
                    if (result) {
                        loginViewModel.showPasswordResetSuccessDialog()
                    } else {
                        loginViewModel.showPasswordResetFailureDialog()
                    }
                }
            },
            dismissDialog = { loginViewModel.dismissLoginDialogs() },
            uiState = uiState
        )
    }
}

fun NavGraphBuilder.registrationScreen(
    navigateToHomeScreen: () -> Unit
) {
    composable(
        route = Screen.Registration.route
    ) {
        val registrationViewModel: RegistrationViewModel = hiltViewModel()
        val uiState by registrationViewModel.uiState.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()

        RegistrationScreen(
            uiState = uiState,
            dismissDialog = { registrationViewModel.hideDialog() },
            registrationClicked = { email, password, firstName, lastName, username ->
                coroutineScope.launch {
                    registrationViewModel.disableUI()
                    val result = registrationViewModel.registerUser(
                        email = email,
                        password = password,
                        firstName = firstName,
                        lastName = lastName,
                        username = username
                    )
                    registrationViewModel.enableUI()
                    if (result) {
                        navigateToHomeScreen()
                    } else {
                        registrationViewModel.showDialog()
                    }
                }
            }
        )
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

fun NavGraphBuilder.hostingScreen(
    navController: NavHostController
) {
    composable(
        route = Screen.Hosting.route
    ) {
        val userViewModel: UserViewModel = it.sharedViewModel(navController = navController)
        val hostingList by userViewModel.hostingTournamentList.collectAsStateWithLifecycle()

        HostingScreen(
            hostingTournamentList = hostingList,
            floatingActionButtonClick = {
                navController.navigate(Screen.TournamentCreation.route)
            },
            tournamentCardClick = { tournament ->
                userViewModel.updateViewingTournament(tournament)
                userViewModel.viewingTournamentId = tournament.id.toString()
                navController.navigate(Screen.EditTournament.route)
            }
        )
    }
}

fun NavGraphBuilder.tournamentCreationScreen() {
    composable(
        route = Screen.TournamentCreation.route
    ) {
        TournamentCreationScreen()
    }
}

fun NavGraphBuilder.editTournamentScreen(navController: NavHostController) {
    composable(
        route = Screen.EditTournament.route
    ) {
        val userViewModel: UserViewModel = it.sharedViewModel(navController = navController)
        val editTourneyViewModel: EditTournamentScreenViewModel = hiltViewModel()
        val coroutineScope = rememberCoroutineScope()

        val uiState by editTourneyViewModel.uiState.collectAsStateWithLifecycle()
        val tournament by userViewModel.viewingTournament.collectAsStateWithLifecycle()

        EditTournamentScreen(
            tournament = tournament,
            uiState = uiState,
            changeBracketDialogState = { generate ->
                if (generate) {
                    coroutineScope.launch {
                        editTourneyViewModel.generateBracket(tournament)
                    }
                }
                editTourneyViewModel.changeBracketGenerationDialogState(false)
            },
            changeClosedDialogState = { state ->
                editTourneyViewModel.changeClosedDialogState(state)
            },
            changeDeleteDialogState = { delete ->
                if (delete) {
                    coroutineScope.launch {
                        editTourneyViewModel.deleteTournament(
                            tournamentId = tournament.id!!,
                            userId = userViewModel.user.value.userId!!,
                            removeDeletedTournamentFromFlow = { id ->
                                userViewModel.removeDeletedTournamentFromFlow(id)
                            }
                        )
                        navController.popBackStack()
                    }
                } else {
                    editTourneyViewModel.changeDeleteDialogState(false)
                }
            },
            changeOpenedDialogState = { state ->
                editTourneyViewModel.changeOpenedDialogState(state)
            },
            bracketOnClick = {
                if (tournament.rounds.isNullOrEmpty()) {
                    editTourneyViewModel.changeBracketGenerationDialogState(true)
                } else {
                    navController.navigate(Screen.Bracket.route)
                }
            },
            lockOnClick = {
                when (tournament.status) {
                    TournamentStatus.REGISTERING.status -> {
                        editTourneyViewModel.changeClosedDialogState(true)
                        editTourneyViewModel.updateTournamentStatus(
                            id = tournament.id!!,
                            status = TournamentStatus.CLOSED.status
                        )
                    }
                    TournamentStatus.CLOSED.status -> {
                        editTourneyViewModel.changeOpenedDialogState(true)
                        editTourneyViewModel.updateTournamentStatus(
                            id = tournament.id!!,
                            status = TournamentStatus.REGISTERING.status
                        )
                    }
                }
            },
            participantsOnClick = {
                navController.navigate(Screen.Participants.route)
            },
            startOnClick = {
                // TODO: if bracket empty, ask for generation
                // TODO: make end tournament if started
            },
            deleteOnClick = {
                editTourneyViewModel.changeDeleteDialogState(true)
            }
        )
    }
}

fun NavGraphBuilder.bracketScreen() {
    composable(
        route = Screen.Bracket.route
    ) {
        BracketScreen()
    }
}

fun NavGraphBuilder.participantsScreen(
    navController: NavHostController
) {
    composable(
        route = Screen.Participants.route
    ) {
        val userViewModel: UserViewModel = it.sharedViewModel(navController = navController)
        val participantsViewModel: ParticipantsScreenViewModel = hiltViewModel()
        val coroutineScope = rememberCoroutineScope()

        val uiState by participantsViewModel.uiState.collectAsStateWithLifecycle()
        val participantList by userViewModel.viewingTournament.collectAsStateWithLifecycle()

        ParticipantsScreen(
            uiState = uiState,
            participantList = participantList.participants,
            floatingActionButtonClick = {
                participantsViewModel.changeAddPlayerDialogVisibility(true)
            },
            dropPlayerOnClick = { participant ->
                participantsViewModel.changeDropPlayerDialogVisibility(true)
                participantsViewModel.changeSelectedParticipant(participant)
            },
            viewMatchesOnClick = { participant ->
                // TODO: Nav to matches screen
            },
            closeCannotAddDialog = {
                participantsViewModel.changeCannotAddPlayerDialogVisibility(false)
            },
            closeDropPlayerDialog = { dropping ->
                if (dropping) {
                    participantsViewModel.changeUIEnabled(false)
                    coroutineScope.launch {
                        participantsViewModel.dropExistingPlayer(
                            tournamentId = userViewModel.viewingTournament.value.id!!,
                            tournamentStatus = userViewModel.viewingTournament.value.status
                        )
                        participantsViewModel.changeDropPlayerDialogVisibility(false)
                        participantsViewModel.changeUIEnabled(true)
                    }
                } else {
                    participantsViewModel.changeDropPlayerDialogVisibility(false)
                }
            },
            closeAddPlayerDialog = { adding, username ->
                if (adding && !username.isNullOrBlank()) {
                    participantsViewModel.changeUIEnabled(false)
                    coroutineScope.launch {
                        participantsViewModel.addNewPlayerToTournament(
                            tournamentId = userViewModel.viewingTournament.value.id!!,
                            participantUserName = username
                        )
                        participantsViewModel.changeAddPlayerDialogVisibility(false)
                        participantsViewModel.changeAddParticipantText("")
                        participantsViewModel.changeUIEnabled(true)
                    }
                } else {
                    participantsViewModel.changeAddPlayerDialogVisibility(false)
                }
            },
            changeAddPlayerTextFieldValue = { username ->
                participantsViewModel.changeAddParticipantText(username)
            }
        )
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
    val navGraphRoute = destination.parent?.parent?.route ?: return hiltViewModel()
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
