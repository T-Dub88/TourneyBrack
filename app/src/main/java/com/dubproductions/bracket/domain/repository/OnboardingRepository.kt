package com.dubproductions.bracket.domain.repository

interface OnboardingRepository {
    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String,
    ): Boolean

    suspend fun signInUser(
        email: String,
        password: String
    ): Boolean

    suspend fun resetPassword(
        email: String
    ): Boolean

    suspend fun checkLoginStatus(): Boolean

}
