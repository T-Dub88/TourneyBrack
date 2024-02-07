package com.dubproductions.bracket.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.presentation.ui.screen.main.hosting.EditTournamentScreen
import com.dubproductions.bracket.presentation.ui.screen.main.hosting.HostingScreen
import com.dubproductions.bracket.presentation.ui.screen.main.hosting.TournamentCreationScreen
import com.dubproductions.bracket.presentation.ui.screen.main.hosting.participant.ParticipantsScreen
import com.dubproductions.bracket.presentation.viewmodel.CreationViewModel
import com.dubproductions.bracket.presentation.viewmodel.EditTournamentViewModel
import com.dubproductions.bracket.presentation.viewmodel.ParticipantsViewModel
import com.dubproductions.bracket.presentation.viewmodel.SharedViewModel
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
//        bracketScreen(navController)
        participantsScreen(navController)
//        participantMatchesScreen(navController)
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
//                if (generate) {
//                    coroutineScope.launch {
//                        editTourneyViewModel.generateBracket(tournament)
//                    }
//                }
//                editTourneyViewModel.changeBracketGenerationDialogState(false)
            },
            changeClosedDialogState = { state ->
                editTourneyViewModel.changeClosedDialogState(state)
            },
            changeDeleteDialogState = { delete ->
//                if (delete) {
//                    coroutineScope.launch {
//                        editTourneyViewModel.deleteTournament(
//                            tournamentId = tournament.tournamentId,
//                            userId = sharedViewModel.user.value.userId,
//                            removeDeletedTournamentFromFlow = { id ->
//                                sharedViewModel.removeDeletedTournamentFromFlow(id)
//                            }
//                        )
//                        navController.popBackStack()
//                    }
//                } else {
//                    editTourneyViewModel.changeDeleteDialogState(false)
//                }
            },
            changeOpenedDialogState = { state ->
                editTourneyViewModel.changeOpenedDialogState(state)
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
                // TODO: if bracket empty, ask for generation
                // TODO: make end tournament if started
            },
            deleteOnClick = {
                editTourneyViewModel.changeDeleteDialogState(true)
            }
        )
    }
}
//
//fun NavGraphBuilder.bracketScreen(navController: NavHostController) {
//    composable(
//        route = Screen.Bracket.route
//    ) {
//        val userViewModel: SharedViewModel = it.sharedViewModel(navController = navController)
//        val participantMatchesViewModel: ParticipantMatchesViewModel = hiltViewModel()
//        val coroutineScope = rememberCoroutineScope()
//
//        val tournament by userViewModel.viewingTournament.collectAsStateWithLifecycle()
//
//        BracketScreen(
//            tournament = tournament,
//            declareWinner = { winnerId, roundNum, matchId ->
//                coroutineScope.launch {
//                    participantMatchesViewModel.declareMatchWinner(
//                        matchId = matchId,
//                        round = tournament.rounds?.find { round ->  round.roundNumber == roundNum }!!,
//                        tournamentId = tournament.id!!,
//                        winnerId = winnerId
//                    )
//                }
//            },
//            editMatch = { matchId, roundNum ->
//                coroutineScope.launch {
//                    participantMatchesViewModel.editMatch(
//                        matchId = matchId,
//                        participantList = tournament.participants,
//                        round = tournament.rounds?.find { round -> round.roundNumber == roundNum }!!,
//                        tournamentId = tournament.id!!
//                    )
//                }
//            }
//        )
//    }
//}
//
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
//                if (dropping) {
//                    participantsViewModel.changeUIEnabled(false)
//                    coroutineScope.launch {
//                        participantsViewModel.dropExistingPlayer(
//                            tournamentId = sharedViewModel.viewingTournamentId,
//                            tournamentStatus = sharedViewModel.viewingTournament.value.status
//                        )
//                        participantsViewModel.changeDropPlayerDialogVisibility(false)
//                        participantsViewModel.changeUIEnabled(true)
//                    }
//                } else {
//                    participantsViewModel.changeDropPlayerDialogVisibility(false)
//                }
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
//
//fun NavGraphBuilder.participantMatchesScreen(navController: NavHostController) {
//    composable(
//        route = Screen.ParticipantMatches.route
//    ) {
//        val userViewModel: SharedViewModel = it.sharedViewModel(navController = navController)
//        val participantMatchesViewModel: ParticipantMatchesViewModel = hiltViewModel()
//        val coroutineScope = rememberCoroutineScope()
//
//        val tournament by userViewModel.viewingTournament.collectAsStateWithLifecycle()
//        val viewingParticipant by userViewModel.viewingParticipant.collectAsStateWithLifecycle()
//
//        ParticipantMatchesScreen(
//            participantList = tournament.participants,
//            matchList = createMatchList(
//                tournament = tournament,
//                userId = viewingParticipant.userId
//            ),
//            tournamentStatus = tournament.status,
//            declareWinner = { winnerId, roundNum, matchId ->
//                coroutineScope.launch {
//                    participantMatchesViewModel.declareMatchWinner(
//                        winnerId = winnerId,
//                        tournamentId = tournament.id!!,
//                        round = tournament.rounds?.find { round -> round.roundNumber == roundNum }!!,
//                        matchId = matchId
//                    )
//                }
//            },
//            editMatch = { matchId, roundNum ->
//                coroutineScope.launch {
//                    participantMatchesViewModel.editMatch(
//                        matchId = matchId,
//                        participantList = tournament.participants,
//                        round = tournament.rounds?.find { round -> round.roundNumber == roundNum }!!,
//                        tournamentId = tournament.id!!
//                    )
//                }
//            }
//        )
//    }
//}
//
//private fun createMatchList(
//    tournament: FirestoreTournamentData,
//    userId: String
//): List<FirestoreMatchData> {
//    val matchList: MutableList<FirestoreMatchData> = mutableListOf()
//    tournament.rounds?.let { roundsList ->
//        for (round in roundsList) {
//            val match = round.matches.find {
//                it.playerOneId == userId || it.playerTwoId == userId
//            }
//            match?.let {
//                matchList.add(it)
//            }
//        }
//    }
//    return matchList
//}
