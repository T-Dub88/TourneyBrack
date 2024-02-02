package com.dubproductions.bracket.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.utils.status.MatchStatus
import com.dubproductions.bracket.domain.model.Round
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParticipantMatchesViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepositoryImpl
): ViewModel() {
    suspend fun declareMatchWinner(
        winnerId: String?,
        tournamentId: String,
        round: Round,
        matchId: String
    ) {
        tournamentRepository.removeOldRoundData(
            tournamentId,
            round
        )

        tournamentRepository.updateMatchResult(
            tournamentId = tournamentId,
            updatedRound = updateRound(round, matchId, winnerId)
        )

    }

    private fun updateRound(
        round: Round,
        matchId: String,
        winnerId: String?
    ): Round {
        val matchToUpdate = round.matches.find { it.matchId == matchId }
        if (matchToUpdate != null) {
            matchToUpdate.declareWinner(winnerId)
            matchToUpdate.setMatchStatus(MatchStatus.COMPLETE.status)
            if (winnerId.isNullOrEmpty()) {
                matchToUpdate.declareTie()
            }
        }
        return round
    }

    private fun resetMatchInRound(
        round: Round,
        matchId: String
    ): Round {
        val matchToReset = round.matches.find { it.matchId == matchId }
        if (matchToReset != null) {
            matchToReset.winnerId = null
            matchToReset.tie = null
            matchToReset.status = MatchStatus.PENDING.status
        }
        return round
    }

    suspend fun editMatch(
        matchId: String,
        round: Round,
        tournamentId: String,
        participantList: List<Participant>
    ) {
        for (match in round.matches) {
            viewModelScope.launch {
                removeMatchIdFromParticipant(
                    participantId = match.playerOneId,
                    matchId = matchId,
                    participantList = participantList
                )
            }

            match.playerTwoId?.let {
                viewModelScope.launch {
                    removeMatchIdFromParticipant(
                        participantId = it,
                        matchId = matchId,
                        participantList = participantList
                    )
                }
            }
        }

        tournamentRepository.removeOldRoundData(
            tournamentId,
            round
        )

        viewModelScope.launch {
            tournamentRepository.updateParticipantList(
                id = tournamentId,
                participants = participantList
            )
        }

        viewModelScope.launch {
            tournamentRepository.updateMatchResult(
                tournamentId = tournamentId,
                updatedRound = resetMatchInRound(round, matchId)
            )
        }
    }

    private fun removeMatchIdFromParticipant(
        participantList: List<Participant>,
        participantId: String,
        matchId: String
    ) {
        val participant = participantList.find { it.userId == participantId }
        participant?.matches?.removeIf { it == matchId }

    }

}
