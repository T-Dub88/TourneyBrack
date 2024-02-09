package com.dubproductions.bracket.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.repository.TournamentRepository
import com.dubproductions.bracket.presentation.ui.state.ParticipantsUIState
import com.dubproductions.bracket.utils.status.TournamentStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ParticipantsViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository
): ViewModel() {

    private val _uiState: MutableStateFlow<ParticipantsUIState> = MutableStateFlow(
        ParticipantsUIState()
    )
    val uiState: StateFlow<ParticipantsUIState> = _uiState.asStateFlow()

    private fun updateUIState(newUIState: ParticipantsUIState) {
        tournamentRepository
        _uiState.update {
            newUIState
        }
    }

    fun changeSelectedParticipant(participant: Participant) {
        val newUIState = uiState.value.copy(
            selectedParticipant = participant
        )
        updateUIState(newUIState)
    }

    fun changeAddPlayerDialogVisibility(visible: Boolean) {
        val newUIState = uiState.value.copy(
            displayAddPlayerDialog = visible
        )
        updateUIState(newUIState)
    }

    fun changeDropPlayerDialogVisibility(visible: Boolean) {
        val newUIState = uiState.value.copy(
            displayDropPlayerDialog = visible
        )
        updateUIState(newUIState)
    }

    fun changeCannotAddPlayerDialogVisibility(visible: Boolean) {
        val newUIState = uiState.value.copy(
            displayCannotAddDialog = visible
        )
        updateUIState(newUIState)
    }

    fun changeUIEnabled(enabled: Boolean) {
        val newUIState = uiState.value.copy(
            enabled = enabled
        )
        updateUIState(newUIState)
    }

    fun changeAddParticipantText(username: String) {
        val newUIState = uiState.value.copy(
            addPlayerTextFieldValue = username
        )
        updateUIState(newUIState)
    }

    suspend fun addNewPlayerToTournament(
        tournamentId: String,
        participantUserName: String
    ) {
        tournamentRepository.addParticipantData(
            tournamentId = tournamentId,
            participant = createParticipant(participantUserName),
            false
        )
    }

    private fun createParticipant(enteredText: String): Participant {
        return Participant(
            userId = makeRandomString(),
            username = enteredText,
            dropped = false,
            points = 0.0,
            opponentsAvgPoints = 0.0,
            opponentsOpponentsAvgPoints = 0.0
        )
    }

    private fun makeRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (0..4).map { allowedChars.random() }.joinToString("")
    }

    suspend fun dropExistingPlayer(
        tournamentId: String,
        tournamentStatus: String
    ) {
        val participantId = uiState.value.selectedParticipant.userId

        when (tournamentStatus) {
            TournamentStatus.PLAYING.statusString -> {
                tournamentRepository.dropParticipant(
                    tournamentId = tournamentId,
                    participantId = participantId
                )
            }
            TournamentStatus.REGISTERING.statusString,
            TournamentStatus.CLOSED.statusString -> {
                tournamentRepository.deleteParticipant(
                    tournamentId = tournamentId,
                    participantId = participantId,
                    deletedTournament = false
                )
            }
        }
    }

}
