package com.dubproductions.bracket.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.data.Tournament
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import com.dubproductions.bracket.data.ui_state.EditTournamentUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTournamentViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepositoryImpl
): ViewModel() {

    private val _uiState: MutableStateFlow<EditTournamentUIState> = MutableStateFlow(
        EditTournamentUIState()
    )
    val uiState: StateFlow<EditTournamentUIState> = _uiState.asStateFlow()

    private fun updateUIState(newUIState: EditTournamentUIState) {
        _uiState.update {
            newUIState
        }
    }

    fun changeOpenedDialogState(display: Boolean) {
        val newUIState = uiState.value.copy(
            displayOpenedDialog = display
        )
        updateUIState(newUIState)
    }

    fun changeClosedDialogState(display: Boolean) {
        val newUIState = uiState.value.copy(
            displayClosedDialog = display
        )
        updateUIState(newUIState)
    }

    fun changeBracketGenerationDialogState(display: Boolean) {
        val newUIState = uiState.value.copy(
            displayBracketGenerationDialog = display
        )
        updateUIState(newUIState)
    }

    fun changeDeleteDialogState(display: Boolean) {
        val newUIState = uiState.value.copy(
            displayDeleteTournamentDialog = display
        )
        updateUIState(newUIState)
    }

    fun updateTournamentStatus(id: String, status: String) {
        viewModelScope.launch {
            tournamentRepository.updateTournamentStatus(id, status)
        }
    }

    suspend fun generateBracket(tournament: Tournament) {

        tournament.id?.let { tournamentId ->
            viewModelScope.async {
                tournament.createNextRound()
            }.await()

            tournament.rounds?.let { rounds ->
                val roundsJob = viewModelScope.async {
                    tournamentRepository.updateTournamentRounds(
                        id = tournamentId,
                        rounds = rounds
                    )
                }

                val participantsJob = viewModelScope.async {
                    tournamentRepository.updateParticipantList(
                        id = tournamentId,
                        participants = tournament.participants
                    )
                }

                awaitAll(roundsJob, participantsJob)

            }
        }
    }

    suspend fun deleteTournament(
        tournamentId: String,
        userId: String,
        removeDeletedTournamentFromFlow: (String) -> Unit
    ) {
        tournamentRepository.removeTournamentListener(tournamentId)
        tournamentRepository.removeTournamentFromDatabase(
            tournamentId = tournamentId,
            userId = userId
        )
        removeDeletedTournamentFromFlow(tournamentId)
    }

}