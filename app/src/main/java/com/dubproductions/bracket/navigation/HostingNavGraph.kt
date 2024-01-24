package com.dubproductions.bracket.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.data.TournamentStatus
import com.dubproductions.bracket.ui.main.hosting.BracketScreen
import com.dubproductions.bracket.ui.main.hosting.EditTournamentScreen
import com.dubproductions.bracket.ui.main.hosting.HostingScreen
import com.dubproductions.bracket.ui.main.hosting.TournamentCreationScreen
import com.dubproductions.bracket.ui.main.hosting.participant.ParticipantMatchesScreen
import com.dubproductions.bracket.ui.main.hosting.participant.ParticipantsScreen
import com.dubproductions.bracket.viewmodel.CreationScreenViewModel
import com.dubproductions.bracket.viewmodel.EditTournamentScreenViewModel
import com.dubproductions.bracket.viewmodel.ParticipantsScreenViewModel
import com.dubproductions.bracket.viewmodel.UserViewModel
import kotlinx.coroutines.launch

fun NavGraphBuilder.hostingNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.Hosting.route,
        route = Map.Hosting.route
    ) {
        hostingScreen(navController)
        tournamentCreationScreen(navController)
        editTournamentScreen(navController)
        bracketScreen()
        participantsScreen(navController)
        participantMatchesScreen()
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

fun NavGraphBuilder.tournamentCreationScreen(navController: NavHostController) {
    composable(
        route = Screen.TournamentCreation.route
    ) {
        val creationViewModel: CreationScreenViewModel = hiltViewModel()
        val coroutineScope = rememberCoroutineScope()
        val uiState by creationViewModel.uiState.collectAsStateWithLifecycle()

        TournamentCreationScreen(
            uiState = uiState,
            createTournament = { name, participants, type ->
                creationViewModel.changeUIEnabled(false)
                coroutineScope.launch {
                    creationViewModel.createTournament(
                        name = name,
                        participants = participants,
                        type = type
                    )
                }
            },
            dismissDialog = { success ->
                creationViewModel.updateCreationState(null)
                if (success) {
                    navController.popBackStack()
                } else {
                    creationViewModel.changeUIEnabled(true)
                }
            }
        )
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
