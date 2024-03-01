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

            launch {
                tournamentRepository.addMatchResults(tournamentId, roundId, matchResults)
            }


            if (match.tie == true && !match.playerTwoId.isNullOrEmpty()) {
                launch {
                    tournamentRepository.updateParticipantPoints(tournamentId, match.playerOneId, 0.5)
                }
                launch {
                    tournamentRepository.updateParticipantPoints(tournamentId, match.playerTwoId, 0.5)
                }
            }

            winnerId?.let {
                launch {
                    tournamentRepository.updateParticipantPoints(tournamentId, it, 1.0)
                }
            }

        }

    }

    private fun pointDeductionCalculation(
        match: Match,
        participantId: String
    ): Double {
        return if (match.winnerId == participantId) {
            -1.0
        } else if (match.tie == true) {
            -0.5
        } else {
            0.0
        }
    }

    private fun reduceScores(
        selectedMatch: Match,
        tournament: Tournament
    ) {
        // Reduce tied points if selected match was a tie
        if (selectedMatch.tie == true && !selectedMatch.playerTwoId.isNullOrEmpty()) {
            viewModelScope.launch {
                tournamentRepository.updateParticipantPoints(
                    tournamentId = tournament.tournamentId,
                    participantId = selectedMatch.playerOneId,
                    earnedPoints = -0.5
                )
            }

            viewModelScope.launch {
                tournamentRepository.updateParticipantPoints(
                    tournamentId = tournament.tournamentId,
                    participantId = selectedMatch.playerTwoId,
                    earnedPoints = -0.5
                )
            }
        }

        // Remove point from winner of selected match
        selectedMatch.winnerId?.let {
            viewModelScope.launch {
                tournamentRepository.updateParticipantPoints(
                    tournamentId = tournament.tournamentId,
                    participantId = it,
                    earnedPoints = -1.0
                )
            }
        }

    }

    private fun deleteFutureRounds(
        tournamentId: String,
        roundsList: List<Round>,
        selectRoundNum: Int
    ) {
        val roundsToRemove = roundsList.filter { it.roundNum > selectRoundNum }

        for (round in roundsToRemove) {
            viewModelScope.launch {
                for (match in round.matches) {

                    launch {
                        tournamentRepository.deleteMatch(
                            tournamentId = tournamentId,
                            roundId = round.roundId,
                            matchId = match.matchId
                        )
                    }

                    // Remove match points and info from player one
                    launch {
                        tournamentRepository.removeMatchIdAndOpponentIdFromParticipant(
                            tournamentId = tournamentId,
                            matchId = match.matchId,
                            participantId = match.playerOneId,
                            opponentId = match.playerTwoId,
                            participantPointDeduction = pointDeductionCalculation(match, match.playerOneId)
                        )
                    }

                    // Remove match points and info from player two
                    match.playerTwoId?.let {
                        launch {
                            tournamentRepository.removeMatchIdAndOpponentIdFromParticipant(
                                tournamentId = tournamentId,
                                matchId = match.matchId,
                                participantId = it,
                                opponentId = match.playerOneId,
                                participantPointDeduction = pointDeductionCalculation(match, it)
                            )
                        }
                    }
                }


                tournamentRepository.deleteRound(tournamentId, round.roundId)

                tournamentRepository.removeRoundId(
                    tournamentId,
                    round.roundId
                )

            }
        }

    }

    fun editMatch(
        matchId: String,
        selectedRound: Round,
        tournament: Tournament
    ) {
        val selectedMatch = selectedRound.matches.find { it.matchId == matchId } ?: return

        viewModelScope.launch {

            if (selectedRound.roundNum < tournament.setNumberOfRounds() && tournament.status != TournamentStatus.PLAYING.statusString) {
                tournamentRepository.updateTournamentStatus(
                    tournamentId = tournament.tournamentId,
                    status = TournamentStatus.PLAYING.statusString
                )
            } else if (selectedRound.roundNum == tournament.setNumberOfRounds() && tournament.status != TournamentStatus.COMPLETE_ROUNDS.statusString) {
                tournamentRepository.updateTournamentStatus(
                    tournamentId = tournament.tournamentId,
                    status = TournamentStatus.COMPLETE_ROUNDS.statusString
                )
            }

            // Change selected matches state and results
            launch {
                tournamentRepository.addMatchResults(
                    tournamentId = tournament.tournamentId,
                    roundId = selectedRound.roundId,
                    match = selectedMatch.copy(
                        winnerId = null,
                        tie = null,
                        status = MatchStatus.PENDING.statusString
                    )
                )
            }

            reduceScores(selectedMatch, tournament)

            deleteFutureRounds(
                tournament.tournamentId,
                tournament.rounds,
                selectedRound.roundNum
            )

        }
    }

}
