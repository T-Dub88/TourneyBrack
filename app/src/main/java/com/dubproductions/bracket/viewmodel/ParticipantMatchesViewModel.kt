package com.dubproductions.bracket.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.data.status.MatchStatus
import com.dubproductions.bracket.data.Round
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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
        viewModelScope.launch {
            tournamentRepository.removeOldRoundData(
                tournamentId,
                round
            )
        }

        viewModelScope.async {
            tournamentRepository.updateMatchResult(
                tournamentId = tournamentId,
                updatedRound = updateRound(round, matchId, winnerId)
            )
        }.await()
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

}
