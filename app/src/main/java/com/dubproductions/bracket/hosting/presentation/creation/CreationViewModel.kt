package com.dubproductions.bracket.hosting.presentation.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.core.data.tournament.RawTournament
import com.dubproductions.bracket.core.domain.model.participant.Participant
import com.dubproductions.bracket.core.domain.repository.TournamentRepository
import com.dubproductions.bracket.core.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreationViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<TournamentCreationState> = MutableStateFlow(
        TournamentCreationState()
    )
    val uiState: StateFlow<TournamentCreationState> = _uiState.asStateFlow()

    private fun updateUIState(newUIState: TournamentCreationState) {
        tournamentRepository
        _uiState.update {
            newUIState
        }
    }

    fun updateCreationState(status: Boolean?) {
        val newUIState = uiState.value.copy(
            successfulCreation = status
        )
        updateUIState(newUIState)
    }

    fun changeUIEnabled(enabled: Boolean) {
        val newUIState = uiState.value.copy(
            screenEnabled = enabled
        )
        updateUIState(newUIState)
    }

    suspend fun createTournament(
        name: String,
        type: String,
        participants: String
    ) {
        val tournamentId = makeRandomString()
        val tournament = RawTournament(
            hostId = userRepository.fetchUserId(),
            tournamentId = tournamentId,
            name = name,
            type = type,
            participantIds = createParticipantList(tournamentId, participants)
        )

        val result = tournamentRepository.createTournament(tournament = tournament)
        updateCreationState(result)
    }

    private fun createParticipantList(tournamentId: String, participants: String): List<String> {
        // TODO: Participants added before tournament added verification bug
        var participant = ""
        val participantIdList = mutableListOf<String>()

        for (i in participants) {
            participant = when (i) {
                ',' -> {
                    val trimmedParticipant = participant.trim()
                    if (trimmedParticipant != "") {
                        val createdParticipant = createParticipant(trimmedParticipant)
                        participantIdList.add(createdParticipant.userId)
                        addParticipantToDatabase(tournamentId, createdParticipant)
                    }
                    ""
                }

                else -> {
                    participant.plus(i)
                }
            }
        }
        val trimmedParticipant = participant.trim()
        if (trimmedParticipant != "") {
            val createdParticipant = createParticipant(trimmedParticipant)
            participantIdList.add(createdParticipant.userId)
            addParticipantToDatabase(tournamentId, createdParticipant)
        }
        return participantIdList
    }

    private fun createParticipant(enteredText: String): Participant {
        return Participant(
            userId = makeRandomString(),
            username = enteredText
        )
    }

    private fun addParticipantToDatabase(tournamentId: String, participant: Participant) {
        viewModelScope.launch {
            tournamentRepository.addParticipantData(tournamentId, participant, true)
        }
    }

    private fun makeRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (0..4).map { allowedChars.random() }.joinToString("")
    }

}
