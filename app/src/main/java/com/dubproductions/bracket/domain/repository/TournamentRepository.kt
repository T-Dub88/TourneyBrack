package com.dubproductions.bracket.domain.repository

import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.model.Tournament

interface TournamentRepository {

    suspend fun fetchCompletedTournamentData(tournamentId: String): Tournament

    fun fetchHostingTournamentData(tournamentId: String, onComplete: (Tournament) -> Unit)

    suspend fun addParticipantData(tournamentId: String, participant: Participant)

    suspend fun createTournament(tournament: Tournament): Boolean

//    suspend fun createTournament(
//        tournament: Tournament
//    ): Boolean
//
//    suspend fun deleteTournament(
//        tournamentId: String,
//        userId: String
//    ): Boolean
//
//    suspend fun updateTournamentStatus(
//        tournamentId: String,
//        status: TournamentStatus
//    ): Boolean
//
//    suspend fun addParticipant(
//        tournamentId: String,
//        participantId: String,
//        participant: Participant
//    ): Boolean
//
//    suspend fun removeParticipant(
//        tournamentId: String,
//        participantId: String
//    ): Boolean
//
//    suspend fun dropParticipant(
//        tournamentId: String,
//        participantId: String
//    ): Boolean
//
//    suspend fun updateMatchResult(
//        matchId: String,
//        winnerId: String?,
//        loserId: String?
//    ): Boolean

}
