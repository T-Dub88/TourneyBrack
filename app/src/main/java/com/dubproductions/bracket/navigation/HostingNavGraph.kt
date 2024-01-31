package com.dubproductions.bracket.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.data.Match
import com.dubproductions.bracket.data.Tournament
import com.dubproductions.bracket.data.TournamentStatus
import com.dubproductions.bracket.ui.main.hosting.BracketScreen
import com.dubproductions.bracket.ui.main.hosting.EditTournamentScreen
import com.dubproductions.bracket.ui.main.hosting.HostingScreen
import com.dubproductions.bracket.ui.main.hosting.TournamentCreationScreen
import com.dubproductions.bracket.ui.main.hosting.participant.ParticipantMatchesScreen
import com.dubproductions.bracket.ui.main.hosting.participant.ParticipantsScreen
import com.dubproductions.bracket.viewmodel.CreationViewModel
import com.dubproductions.bracket.viewmodel.EditTournamentViewModel
import com.dubproductions.bracket.viewmodel.ParticipantMatchesViewModel
import com.dubproductions.bracket.viewmodel.ParticipantsViewModel
import com.dubproductions.bracket.viewmodel.UserViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

fun NavGraphBuilder.hostingNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.Hosting.route,
        route = Map.Hosting.route
    ) {
        hostingScreen(navController)
        tournamentCreationScreen(navController)
        editTournamentScreen(navController)
        bracketScreen(navController)
        participantsScreen(navController)
        participantMatchesScreen(navController)
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
        val creationViewModel: CreationViewModel = hiltViewModel()
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
        val editTourneyViewModel: EditTournamentViewModel = hiltViewModel()
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

fun NavGraphBuilder.bracketScreen(navController: NavHostController) {
    composable(
        route = Screen.Bracket.route
    ) {
        val userViewModel: UserViewModel = it.sharedViewModel(navController = navController)
        val participantMatchesViewModel: ParticipantMatchesViewModel = hiltViewModel()
        val coroutineScope = rememberCoroutineScope()

        val tournament by userViewModel.viewingTournament.collectAsStateWithLifecycle()

        BracketScreen(
            tournament = tournament,
            declareWinner = { winnerId, roundNum, matchId ->
                coroutineScope.launch {
                    participantMatchesViewModel.declareMatchWinner(
                        matchId = matchId,
                        round = tournament.rounds?.find { round ->  round.roundNumber == roundNum }!!,
                        tournamentId = tournament.id!!,
                        winnerId = winnerId
                    )
                }
            }
        )
    }
}

fun NavGraphBuilder.participantsScreen(
    navController: NavHostController
) {
    composable(
        route = Screen.Participants.route
    ) {
        val userViewModel: UserViewModel = it.sharedViewModel(navController = navController)
        val participantsViewModel: ParticipantsViewModel = hiltViewModel()
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
                userViewModel.updateViewingParticipant(participant)
                userViewModel.viewingParticipantId = participant.userId
                navController.navigate(Screen.ParticipantMatches.route)
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

fun NavGraphBuilder.participantMatchesScreen(navController: NavHostController) {
    composable(
        route = Screen.ParticipantMatches.route
    ) {
        val userViewModel: UserViewModel = it.sharedViewModel(navController = navController)
        val participantMatchesViewModel: ParticipantMatchesViewModel = hiltViewModel()
        val coroutineScope = rememberCoroutineScope()

        val tournament by userViewModel.viewingTournament.collectAsStateWithLifecycle()
        val viewingParticipant by userViewModel.viewingParticipant.collectAsStateWithLifecycle()

        ParticipantMatchesScreen(
            participantList = tournament.participants,
            matchList = createMatchList(
                tournament = tournament,
                userId = viewingParticipant.userId
            ),
            declareWinner = { winnerId, roundNum, matchId ->
                coroutineScope.launch {
                    participantMatchesViewModel.declareMatchWinner(
                        winnerId = winnerId,
                        tournamentId = tournament.id!!,
                        round = tournament.rounds?.find { round -> round.roundNumber == roundNum }!!,
                        matchId = matchId
                    )
                }
            }
        )
    }
}

private fun createMatchList(
    tournament: Tournament,
    userId: String
): List<Match> {
    val matchList: MutableList<Match> = mutableListOf()
    tournament.rounds?.let { roundsList ->
        for (round in roundsList) {
            val match = round.matches.find {
                it.playerOneId == userId || it.playerTwoId == userId
            }
            match?.let {
                matchList.add(it)
            }
        }
    }
    return matchList
}
