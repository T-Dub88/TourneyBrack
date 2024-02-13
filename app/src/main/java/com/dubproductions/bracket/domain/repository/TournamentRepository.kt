package com.dubproductions.bracket.domain.repository

import com.dubproductions.bracket.data.model.RawRound
import com.dubproductions.bracket.data.model.RawTournament
import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.model.Tournament

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

}
