package com.dubproductions.bracket.viewmodel

import androidx.lifecycle.ViewModel
import com.dubproductions.bracket.data.Participant
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import com.dubproductions.bracket.data.ui_state.ParticipantsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ParticipantsViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepositoryImpl
): ViewModel() {

    private val _uiState: MutableStateFlow<ParticipantsUIState> = MutableStateFlow(
        ParticipantsUIState()
    )
    val uiState: StateFlow<ParticipantsUIState> = _uiState.asStateFlow()

    private fun updateUIState(newUIState: ParticipantsUIState) {
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
        tournamentRepository.addParticipant(
            tournamentId = tournamentId,
            participant = createParticipant(participantUserName)
        )
    }

    private fun createParticipant(enteredText: String): Participant {
        return Participant(
            userId = makeRandomString(),
            username = enteredText,
            dropped = false,
            points = 0.0,
            buchholz = 0.0,
            sonnebornBerger = 0.0
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
        val participant = uiState.value.selectedParticipant

        tournamentRepository.removeParticipant(
            tournamentId = tournamentId,
            participant = participant
        )

        when (tournamentStatus) {
            "playing", "complete" -> {
                participant.dropped = true

                tournamentRepository.dropParticipant(
                    tournamentId = tournamentId,
                    participant = participant
                )
            }
        }
    }

}
