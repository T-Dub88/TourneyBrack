package com.dubproductions.bracket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.data.Participant
import com.dubproductions.bracket.data.Tournament
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import com.dubproductions.bracket.data.ui_state.TournamentCreationUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreationViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepositoryImpl
): ViewModel() {

    private val _uiState: MutableStateFlow<TournamentCreationUIState> = MutableStateFlow(
        TournamentCreationUIState()
    )
    val uiState: StateFlow<TournamentCreationUIState> = _uiState.asStateFlow()

    private fun updateUIState(newUIState: TournamentCreationUIState) {
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
        val tournament = Tournament(
            name = name,
            type = type,
            participants = createParticipantList(participants)
        )

        Log.i("Tournament", "createTournament: $tournament")

        val result = tournamentRepository.createTournament(tournament = tournament)
        updateCreationState(result)
    }

    private suspend fun createParticipantList(participants: String): List<Participant> {
        return viewModelScope.async {
            var participant = ""
            val participantList = mutableListOf<Participant>()

            for (i in participants) {
                participant = when (i) {
                    ',' -> {
                        val trimmedParticipant = participant.trim()
                        if (trimmedParticipant != "") {
                            val createdParticipant = createParticipant(trimmedParticipant)
                            participantList.add(createdParticipant)
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
                participantList.add(createdParticipant)
            }
            participantList
        }.await()
    }

    private fun createParticipant(enteredText: String): Participant {
        return Participant(
            userId = makeRandomString(),
            username = enteredText
        )
    }

    private fun makeRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (0..4).map { allowedChars.random() }.joinToString("")
    }

}
