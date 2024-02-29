package com.dubproductions.bracket.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.model.Round
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.domain.repository.TournamentRepository
import com.dubproductions.bracket.utils.TournamentHousekeeping.setNumberOfRounds
import com.dubproductions.bracket.utils.status.MatchStatus
import com.dubproductions.bracket.utils.status.TournamentStatus
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

    private fun pointDeductionCalculation(
        tournament: Tournament,
        round: Round,
        match: Match,
        participantId: String
    ): Double {
        return if (round == tournament.rounds.last()) {
            0.0
        } else if (match.winnerId == participantId) {
            -1.0
        } else if (match.tie == true) {
            -0.5
        } else {
            0.0
        }
    }

    fun editMatch(
        matchId: String,
        selectedRound: Round,
        tournament: Tournament,
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

            if (selectedRound.roundNum < tournament.setNumberOfRounds() && tournament.status != TournamentStatus.PLAYING.statusString) {
                tournamentRepository.updateTournamentStatus(
                    tournamentId = tournament.tournamentId,
                    status = TournamentStatus.PLAYING.statusString
                )
            }


            for (round in tournament.rounds) {
                launch {
                    if (round.roundNum > selectedRound.roundNum) {

                        for (match in round.matches) {

                            launch {
                                tournamentRepository.deleteMatch(
                                    tournamentId = tournament.tournamentId,
                                    roundId = round.roundId,
                                    matchId = match.matchId
                                )
                            }

                            launch {
                                tournamentRepository.removeMatchIdAndOpponentIdFromParticipant(
                                    tournamentId = tournament.tournamentId,
                                    participantId = match.playerOneId,
                                    participantPointDeduction = pointDeductionCalculation(tournament, round, match, match.playerOneId),
                                    matchId = match.matchId,
                                    opponentId = match.playerTwoId
                                )
                            }

                            match.playerTwoId?.let {
                                launch {
                                    tournamentRepository.removeMatchIdAndOpponentIdFromParticipant(
                                        tournamentId = tournament.tournamentId,
                                        participantId = it,
                                        participantPointDeduction = pointDeductionCalculation(tournament, round, match, it),
                                        matchId = match.matchId,
                                        opponentId = match.playerOneId
                                    )
                                }
                            }

                        }

                        tournamentRepository.deleteRound(
                            tournamentId = tournament.tournamentId,
                            roundId = round.roundId
                        )

                        launch {
                            tournamentRepository.removeRoundId(
                                tournamentId = tournament.tournamentId,
                                roundId = round.roundId
                            )
                        }
                    } else {
                        for (match in round.matches) {
                            launch {
                                tournamentRepository.updateParticipantPoints(
                                    earnedPoints = pointDeductionCalculation(tournament, round, match, match.playerOneId),
                                    participantId = match.playerOneId,
                                    tournamentId = tournament.tournamentId
                                )
                            }

                            match.playerTwoId?.let {
                                launch {
                                    tournamentRepository.updateParticipantPoints(
                                        earnedPoints = pointDeductionCalculation(tournament, round, match, it),
                                        participantId = it,
                                        tournamentId = tournament.tournamentId
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
