package com.dubproductions.bracket.domain.repository

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
    fun fetchUserData(onComplete: (User?) -> Unit)

    suspend fun fetchTournamentData(
        tournamentId: String
    ): Tournament?

    suspend fun createTournament(tournament: Tournament): Boolean

}
