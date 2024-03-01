package com.dubproductions.bracket.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.presentation.ui.screen.main.hosting.BracketScreen
import com.dubproductions.bracket.presentation.ui.screen.main.hosting.EditTournamentScreen
import com.dubproductions.bracket.presentation.ui.screen.main.hosting.HostingScreen
import com.dubproductions.bracket.presentation.ui.screen.main.hosting.TournamentCreationScreen
import com.dubproductions.bracket.presentation.ui.screen.main.hosting.participant.ParticipantMatchesScreen
import com.dubproductions.bracket.presentation.ui.screen.main.hosting.participant.ParticipantsScreen
import com.dubproductions.bracket.presentation.viewmodel.CreationViewModel
import com.dubproductions.bracket.presentation.viewmodel.EditTournamentViewModel
import com.dubproductions.bracket.presentation.viewmodel.ParticipantMatchesViewModel
import com.dubproductions.bracket.presentation.viewmodel.ParticipantsViewModel
import com.dubproductions.bracket.presentation.viewmodel.SharedViewModel
import com.dubproductions.bracket.utils.status.MatchStatus
import com.dubproductions.bracket.utils.status.TournamentStatus
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
        val sharedViewModel = it.sharedViewModel<SharedViewModel>(navController = navController)

        val hostingList by sharedViewModel.hostingTournamentList.collectAsStateWithLifecycle()

        HostingScreen(
            hostingTournamentList = hostingList,
            floatingActionButtonClick = {
                navController.navigate(Screen.TournamentCreation.route)
            },
            tournamentCardClick = { tournament ->
                sharedViewModel.viewingTournamentId = tournament.tournamentId
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
        val sharedViewModel: SharedViewModel = it.sharedViewModel(navController = navController)
        val editTourneyViewModel: EditTournamentViewModel = hiltViewModel()
        val coroutineScope = rememberCoroutineScope()

        val uiState by editTourneyViewModel.uiState.collectAsStateWithLifecycle()
        val tournamentList by sharedViewModel.hostingTournamentList.collectAsStateWithLifecycle()
        val tournament = tournamentList.find { selectedTournament ->
            selectedTournament.tournamentId == sharedViewModel.viewingTournamentId
        }!!

        EditTournamentScreen(
            tournament = tournament,
            uiState = uiState,
            changeBracketDialogState = { generate ->
                editTourneyViewModel.changeBracketGenerationDialogState(false)
                coroutineScope.launch {
                    if (generate) {
                        editTourneyViewModel.enableDisableUIState(false)
                        if (tournament.timeStarted == null) {
                            editTourneyViewModel.startTournament(tournament.tournamentId)
                        }
                        sharedViewModel.generateBracket(tournament.tournamentId)
                        editTourneyViewModel.enableDisableUIState(true)
                    }
                }

            },
            changeClosedDialogState = { state ->
                editTourneyViewModel.changeClosedDialogState(state)
            },
            changeDeleteDialogState = { delete ->
                if (delete) {
                    sharedViewModel.deleteTournament(tournament = tournament)
                    navController.popBackStack()
                } else {
                    editTourneyViewModel.changeDeleteDialogState(false)
                }
            },
            changeOpenedDialogState = { state ->
                editTourneyViewModel.changeOpenedDialogState(state)
            },
            changeIncompleteMatchDialogState = { state ->
                editTourneyViewModel.changeMatchesIncompleteDialogState(state)
            },
            changeNextRoundDialogState = { generate ->
                editTourneyViewModel.changeNewRoundDialogState(false)
                coroutineScope.launch {
                    if (generate) {
                        editTourneyViewModel.enableDisableUIState(false)
                        sharedViewModel.generateBracket(tournament.tournamentId)
                        editTourneyViewModel.enableDisableUIState(true)
                    }

                }

            },
            changeCompleteRoundsDialogState = { complete ->
                editTourneyViewModel.changeCompleteRoundsDialogState(false)
                if (complete) {
                    coroutineScope.launch {
                        editTourneyViewModel.updateTournamentStatus(
                            tournament.tournamentId,
                            TournamentStatus.COMPLETE_TOURNAMENT.statusString
                        )
                        sharedViewModel.updateScores(tournament.tournamentId)
                    }
                    editTourneyViewModel.changeCompleteTournamentDialogState(true)
                }

            },
            changeCompleteTournamentDialogState = { complete ->
                if (complete) {
                    sharedViewModel.completeTournament(tournament)
                    navController.popBackStack()
                } else {
                    editTourneyViewModel.changeCompleteTournamentDialogState(false)
                }

            },
            bracketOnClick = {
                if (tournament.rounds.isEmpty()) {
                    editTourneyViewModel.changeBracketGenerationDialogState(true)
                } else {
                    navController.navigate(Screen.Bracket.route)
                }
            },
            lockOnClick = {
                when (tournament.status) {
                    TournamentStatus.REGISTERING.statusString -> {
                        editTourneyViewModel.changeClosedDialogState(true)
                        editTourneyViewModel.updateTournamentStatus(
                            id = tournament.tournamentId,
                            status = TournamentStatus.CLOSED.statusString
                        )
                    }
                    TournamentStatus.CLOSED.statusString -> {
                        editTourneyViewModel.changeOpenedDialogState(true)
                        editTourneyViewModel.updateTournamentStatus(
                            id = tournament.tournamentId,
                            status = TournamentStatus.REGISTERING.statusString
                        )
                    }
                }
            },
            participantsOnClick = {
                navController.navigate(Screen.Participants.route)
            },
            startOnClick = {
                when (tournament.status) {
                    TournamentStatus.CLOSED.statusString,
                    TournamentStatus.REGISTERING.statusString -> {
                        editTourneyViewModel.changeBracketGenerationDialogState(true)
                    }

                    TournamentStatus.PLAYING.statusString -> {
                        if (checkForIncompleteMatches(tournament)) {
                            editTourneyViewModel.changeMatchesIncompleteDialogState(true)
                        } else {
                            editTourneyViewModel.changeNewRoundDialogState(true)
                        }
                    }

                    TournamentStatus.COMPLETE_ROUNDS.statusString -> {
                        if (checkForIncompleteMatches(tournament)) {
                            editTourneyViewModel.changeMatchesIncompleteDialogState(true)
                        } else {
                            editTourneyViewModel.changeCompleteRoundsDialogState(true)
                        }
                    }
                    TournamentStatus.COMPLETE_TOURNAMENT.statusString -> {
                        editTourneyViewModel.changeCompleteTournamentDialogState(true)
                    }
                }
            },
            deleteOnClick = {
                editTourneyViewModel.changeDeleteDialogState(true)
            }
        )
    }
}

private fun checkForIncompleteMatches(tournament: Tournament): Boolean {
    val incompleteMatch = tournament.rounds.last().matches.find {
            match -> match.status == MatchStatus.PENDING.statusString
    }

    return incompleteMatch != null
}

fun NavGraphBuilder.bracketScreen(navController: NavHostController) {
    composable(
        route = Screen.Bracket.route
    ) {
        val sharedViewModel: SharedViewModel = it.sharedViewModel(navController = navController)
        val participantMatchesViewModel: ParticipantMatchesViewModel = hiltViewModel()

        val tournamentsList by sharedViewModel.hostingTournamentList.collectAsStateWithLifecycle()

        val tournament = tournamentsList.find { tourney ->
            tourney.tournamentId == sharedViewModel.viewingTournamentId
        }!!

        BracketScreen(
            tournament = tournament,
            declareWinner = { winnerId, roundNum, match ->
                participantMatchesViewModel.declareMatchWinner(
                    match = match,
                    roundId = tournament.rounds.find { round ->  round.roundNum == roundNum }!!.roundId,
                    tournamentId = tournament.tournamentId,
                    winnerId = winnerId
                )
            },
            editMatch = { matchId, roundNum ->
                participantMatchesViewModel.editMatch(
                    matchId = matchId,
                    selectedRound = tournament.rounds.find { round -> round.roundNum == roundNum }!!,
                    tournament = tournament
                )
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
        val sharedViewModel: SharedViewModel = it.sharedViewModel(navController = navController)
        val participantsViewModel: ParticipantsViewModel = hiltViewModel()
        val coroutineScope = rememberCoroutineScope()

        val uiState by participantsViewModel.uiState.collectAsStateWithLifecycle()
        val tournamentList by sharedViewModel.hostingTournamentList.collectAsStateWithLifecycle()
        val selectedTournament = tournamentList.find { tourney ->
            tourney.tournamentId == sharedViewModel.viewingTournamentId
        }!!

        ParticipantsScreen(
            uiState = uiState,
            participantList = selectedTournament.participants,
            floatingActionButtonClick = {
                participantsViewModel.changeAddPlayerDialogVisibility(true)
            },
            dropPlayerOnClick = { participant ->
                participantsViewModel.changeDropPlayerDialogVisibility(true)
                participantsViewModel.changeSelectedParticipant(participant)
            },
            viewMatchesOnClick = { participant ->
                sharedViewModel.viewingParticipantId = participant.userId
                navController.navigate(Screen.ParticipantMatches.route)
            },
            closeCannotAddDialog = {
                participantsViewModel.changeCannotAddPlayerDialogVisibility(false)
            },
            closeDropPlayerDialog = { dropping ->
                if (dropping) {
                    val status = selectedTournament.status
                    participantsViewModel.changeUIEnabled(false)
                    coroutineScope.launch {
                        participantsViewModel.dropExistingPlayer(selectedTournament)
                        when (status) {
                            TournamentStatus.REGISTERING.statusString,
                            TournamentStatus.CLOSED.statusString -> {
                                val participant = participantsViewModel.uiState.value.selectedParticipant
                                sharedViewModel.removePlayerFromTournamentFlow(participant)
                            }
                        }

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
                            tournamentId = sharedViewModel.viewingTournamentId,
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
        val sharedViewModel: SharedViewModel = it.sharedViewModel(navController = navController)
        val participantMatchesViewModel: ParticipantMatchesViewModel = hiltViewModel()

        val tournaments by sharedViewModel.hostingTournamentList.collectAsStateWithLifecycle()

        val tournament = tournaments.find { tourney -> tourney.tournamentId == sharedViewModel.viewingTournamentId }!!

        ParticipantMatchesScreen(
            participantList = tournament.participants,
            matchList = createMatchList(
                tournament = tournament,
                userId = sharedViewModel.viewingParticipantId
            ),
            tournamentStatus = tournament.status,
            declareWinner = { winnerId, roundNum, match ->
                participantMatchesViewModel.declareMatchWinner(
                    winnerId = winnerId,
                    tournamentId = tournament.tournamentId,
                    roundId = tournament.rounds.find { round -> round.roundNum == roundNum }!!.roundId,
                    match = match
                )
            },
            editMatch = { matchId, roundNum ->
                participantMatchesViewModel.editMatch(
                    matchId = matchId,
                    selectedRound = tournament.rounds.find { round -> round.roundNum == roundNum }!!,
                    tournament = tournament
                )
            }
        )
    }
}

private fun createMatchList(
    tournament: Tournament,
    userId: String
): List<Match> {
    val matchList: MutableList<Match> = mutableListOf()

    for (round in tournament.rounds) {
        val match = round.matches.find {
            it.playerOneId == userId || it.playerTwoId == userId
        }
        match?.let {
            matchList.add(it)
        }
    }

    return matchList
}
