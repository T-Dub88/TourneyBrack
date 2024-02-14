package com.dubproductions.bracket.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.model.Round
import com.dubproductions.bracket.domain.model.Tournament
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

    fun editMatch(
        matchId: String,
        selectedRound: Round,
        tournament: Tournament,
        participantList: List<Participant>
    ) {
        val selectedMatch = selectedRound.matches.find { it.matchId == matchId } ?: return

        viewModelScope.launch {

            tournamentRepository.addMatchResults(
                tournamentId = tournament.tournamentId,
                roundId = selectedRound.roundId,
                match = selectedMatch.copy(
                    winnerId = null,
                    tie = null,
                    status = MatchStatus.PENDING.statusString
                )
            )

            if (selectedRound != tournament.rounds.last()) {
                for (i in selectedRound.roundNum..tournament.rounds.lastIndex) {
                    val roundId = tournament.rounds[i].roundId
                    launch {
                        for (match in tournament.rounds[i].matches) {
                            launch {
                                tournamentRepository.deleteMatch(
                                    tournamentId = tournament.tournamentId,
                                    roundId = roundId,
                                    matchId = match.matchId
                                )
                            }
                            launch {
                                tournamentRepository.removeMatchIdAndOpponentIdFromParticipant(
                                    tournamentId = tournament.tournamentId,
                                    participantId = match.playerOneId,
                                    matchId = matchId,
                                    opponentId = match.playerTwoId
                                )
                            }

                            match.playerTwoId?.let {
                                launch {
                                    tournamentRepository.removeMatchIdAndOpponentIdFromParticipant(
                                        tournamentId = tournament.tournamentId,
                                        participantId = it,
                                        matchId = matchId,
                                        opponentId = match.playerOneId
                                    )
                                }
                            }

                        }

                        tournamentRepository.deleteRound(
                            tournamentId = tournament.tournamentId,
                            roundId = roundId
                        )

                        launch {
                            tournamentRepository.removeRoundId(
                                tournamentId = tournament.tournamentId,
                                roundId = roundId
                            )
                        }
                    }
                }
            }
        }
    }

}
