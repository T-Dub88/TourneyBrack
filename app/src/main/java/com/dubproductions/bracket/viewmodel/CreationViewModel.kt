package com.dubproductions.bracket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.data.Participant
import com.dubproductions.bracket.data.Tournament
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreationViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepositoryImpl
): ViewModel() {

    private val _successfulCreation: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val successfulCreation: StateFlow<Boolean?> = _successfulCreation.asStateFlow()

    fun updateCreationState(status: Boolean?) {
        _successfulCreation.update {
            status
        }
    }

    fun createTournament(
        name: String,
        type: String,
        participants: String
    ) {
        viewModelScope.launch {

            val tournament = Tournament(
                name = name,
                type = type,
                participants = createParticipantList(participants),
                status = "registering"
            )

            Log.i("Tournament", "createTournament: $tournament")

            val result = tournamentRepository.createTournament(tournament = tournament)
            updateCreationState(result)
        }
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

}
