package com.dubproductions.bracket.hosting.presentation.participants

import androidx.lifecycle.ViewModel
import com.dubproductions.bracket.core.domain.model.participant.Participant
import com.dubproductions.bracket.core.domain.model.tournament.Tournament
import com.dubproductions.bracket.core.domain.repository.TournamentRepository
import com.dubproductions.bracket.core.domain.model.match.MatchStatus
import com.dubproductions.bracket.core.domain.model.tournament.TournamentStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ParticipantsViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<ParticipantsState> = MutableStateFlow(
        ParticipantsState()
    )
    val uiState: StateFlow<ParticipantsState> = _uiState.asStateFlow()

    private fun updateUIState(newUIState: ParticipantsState) {
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
            opponentsAveragePoints = 0.0,
            opponentsOpponentsAveragePoints = 0.0
        )
    }

    private fun makeRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (0..4).map { allowedChars.random() }.joinToString("")
    }

    suspend fun dropExistingPlayer(
        tournament: Tournament
    ) {
        val participantId = uiState.value.selectedParticipant.userId

        when (tournament.status) {
            TournamentStatus.PLAYING.statusString -> {
                tournamentRepository.dropParticipant(
                    tournamentId = tournament.tournamentId,
                    participantId = participantId
                )

                val round = tournament.rounds.last()
                val match = round.matches.find {
                    it.playerOneId == participantId || it.playerTwoId == participantId
                } ?: return

                if (match.status == MatchStatus.PENDING.statusString) {
                    val completedMatch = match.copy(
                        winnerId = if (match.playerOneId == participantId) {
                            match.playerTwoId
                        } else {
                            match.playerOneId
                        },
                        tie = false,
                        status = MatchStatus.COMPLETE.statusString
                    )

                    tournamentRepository.addMatchResults(
                        tournamentId = tournament.tournamentId,
                        roundId = round.roundId,
                        match = completedMatch
                    )
                }

            }
            TournamentStatus.REGISTERING.statusString,
            TournamentStatus.CLOSED.statusString -> {
                tournamentRepository.deleteParticipant(
                    tournamentId = tournament.tournamentId,
                    participantId = participantId,
                    deletedTournament = false
                )
            }
        }
    }

}
