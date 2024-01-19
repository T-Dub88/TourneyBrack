package com.dubproductions.bracket.viewmodel

import androidx.lifecycle.ViewModel
import com.dubproductions.bracket.data.Participant
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ParticipantViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepositoryImpl
): ViewModel() {

    private val _selectedParticipant: MutableStateFlow<Participant> = MutableStateFlow(Participant())
    val selectedParticipant: StateFlow<Participant> = _selectedParticipant.asStateFlow()

    fun updateSelectedParticipant(participant: Participant) {
        _selectedParticipant.update {
            participant
        }
    }

    suspend fun removePlayerFromTournament(
        tournamentId: String,
        participant: Participant
    ) {
        tournamentRepository.removeParticipant(
            tournamentId = tournamentId,
            participant = participant
        )
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
        participant: Participant,
        tournamentStatus: String
    ) {

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
