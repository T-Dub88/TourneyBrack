package com.dubproductions.bracket.core.domain.repository

import com.dubproductions.bracket.core.data.round.RawRound
import com.dubproductions.bracket.core.data.tournament.RawTournament
import com.dubproductions.bracket.core.domain.model.match.Match
import com.dubproductions.bracket.core.domain.model.participant.Participant
import com.dubproductions.bracket.core.domain.model.tournament.Tournament

interface TournamentRepository {

    suspend fun fetchCompletedTournamentData(tournamentId: String): Tournament

    fun fetchHostingTournamentData(
        tournamentId: String,
        onComplete: (Tournament) -> Unit
    )

    suspend fun addParticipantData(
        tournamentId: String,
        participant: Participant,
        newTournament: Boolean
    )

    suspend fun createTournament(tournament: RawTournament): Boolean

    fun listenToParticipant(
        tournamentId: String,
        participantId: String,
        onComplete: (Participant) -> Unit
    )

    fun listenToMatch(
        tournamentId: String,
        roundId: String,
        matchId: String,
        onComplete: (Match) -> Unit
    )

    suspend fun updateTournamentStatus(
        tournamentId: String,
        status: String
    )

    suspend fun deleteTournament(
        tournament: Tournament
    ): Boolean

    suspend fun deleteParticipant(
        tournamentId: String,
        participantId: String,
        deletedTournament: Boolean
    ): Boolean

    suspend fun deleteRound(
        tournamentId: String,
        roundId: String
    ): Boolean

    suspend fun removeRoundId(
        tournamentId: String,
        roundId: String
    ): Boolean

    suspend fun deleteMatch(
        tournamentId: String,
        roundId: String,
        matchId: String
    ): Boolean

    suspend fun dropParticipant(
        tournamentId: String,
        participantId: String
    ): Boolean

    suspend fun addNewMatch(
        match: Match,
        tournamentId: String,
        roundId: String
    )

    suspend fun addNewRound(
        round: RawRound,
        tournamentId: String
    ): Boolean

    suspend fun addRoundIdToTournament(
        roundId: String,
        tournamentId: String
    ): Boolean

    suspend fun addMatchResults(
        tournamentId: String,
        roundId: String,
        match: Match
    ): Boolean

    suspend fun startTournament(
        tournamentId: String,
        timestamp: Long
    ): Boolean

    suspend fun removeMatchIdAndOpponentIdFromParticipant(
        tournamentId: String,
        participantId: String,
        participantPointDeduction: Double,
        matchId: String,
        opponentId: String?
    ): Boolean

    suspend fun updateParticipantPoints(
        tournamentId: String,
        participantId: String,
        earnedPoints: Double
    ): Boolean

    suspend fun updateTiebreakers(
        tournamentId: String,
        participantId: String,
        firstTiebreaker: Double,
        secondTiebreaker: Double
    ): Boolean

    suspend fun completeTournament(
        tournament: Tournament
    )

}
