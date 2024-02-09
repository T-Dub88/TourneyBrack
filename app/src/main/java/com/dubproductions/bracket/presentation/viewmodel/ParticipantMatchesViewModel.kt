package com.dubproductions.bracket.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.model.Round
import com.dubproductions.bracket.domain.repository.TournamentRepository
import com.dubproductions.bracket.utils.status.MatchStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParticipantMatchesViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository
): ViewModel() {

    fun declareMatchWinner(
        winnerId: String?,
        tournamentId: String,
        roundId: String,
        match: Match
    ) {
        viewModelScope.launch {

            val matchResults = match.copy(
                winnerId = winnerId,
                tie = winnerId.isNullOrEmpty(),
                status = MatchStatus.COMPLETE.statusString
            )

            tournamentRepository.addMatchResults(tournamentId, roundId, matchResults)

        }

    }

    suspend fun editMatch(
        matchId: String,
        round: Round,
        tournamentId: String,
        participantList: List<Participant>
    ) {

    }

}
