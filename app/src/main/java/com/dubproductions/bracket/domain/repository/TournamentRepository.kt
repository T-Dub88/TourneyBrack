package com.dubproductions.bracket.domain.repository

import com.dubproductions.bracket.data.Participant
import com.dubproductions.bracket.data.Round
import com.dubproductions.bracket.data.Tournament
import com.dubproductions.bracket.data.User

interface TournamentRepository {
    // Onboarding methods
    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String,
    ): Boolean

    suspend fun createUserData(
        userData: User,
    ): Boolean

    fun deleteUserSignup()

    suspend fun signInUser(
        email: String,
        password: String
    ): Boolean

    suspend fun resetPassword(
        email: String
    ): Boolean

    fun checkLoginStatus(): Boolean

    // Update app data methods
    fun fetchUserData(
        onComplete: (User?) -> Unit
    )

    suspend fun fetchTournamentData(
        tournamentId: String
    ): Tournament?

    fun listenToTournament(
        tournamentId: String,
        onComplete: (Tournament?) -> Unit
    )

    suspend fun createTournament(
        tournament: Tournament
    ): Boolean

    suspend fun addTournamentIdToHost(
        tournamentId: String,
        userId: String
    ): Boolean

    suspend fun removeTournamentFromDatabase(
        tournamentId: String,
        userId: String?
    ): Boolean

    suspend fun removeTournamentFromUser(
        userId: String,
        tournamentId: String
    ): Boolean

    fun removeTournamentListener(
        tournamentId: String
    )

    suspend fun updateTournamentStatus(
        id: String,
        status: String
    )

    suspend fun updateTournamentRounds(
        id: String,
        rounds: MutableList<Round>
    )

    suspend fun updateParticipantList(
        id: String,
        participants: List<Participant>
    )

    suspend fun addParticipant(
        tournamentId: String,
        participant: Participant
    )

    suspend fun removeParticipant(
        tournamentId: String,
        participant: Participant
    )

    suspend fun dropParticipant(
        tournamentId: String,
        participant: Participant
    )

    suspend fun updateMatchResult(
        tournamentId: String,
        updatedRound: Round,
    )

}
