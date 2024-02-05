package com.dubproductions.bracket.data.repository

import com.dubproductions.bracket.data.model.FirestoreUserData
import com.dubproductions.bracket.data.remote.FirebaseAuthService
import com.dubproductions.bracket.data.remote.FirestoreService
import com.dubproductions.bracket.domain.repository.OnboardingRepository

class OnboardingRepositoryImpl(
    private val firebaseAuthService: FirebaseAuthService,
    private val firestoreService: FirestoreService
): OnboardingRepository {

    override suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String
    ): Boolean {
        val registrationResult = firebaseAuthService.registerUser(email, password)

        return if (registrationResult) {
            val newUser = FirestoreUserData(
                userId = firebaseAuthService.getSignedInUserId(),
                username = username,
                email = email,
                firstName = firstName,
                lastName = lastName
            )
            val databaseResult = firestoreService.createNewUserData(newUser)
            if (databaseResult) {
                true
            } else {
                !firebaseAuthService.deleteUserAccount()
            }
        } else {
            false
        }

    }

    override suspend fun signInUser(email: String, password: String): Boolean {
        return firebaseAuthService.signInUser(email, password)
    }

    override suspend fun resetPassword(email: String): Boolean {
        return firebaseAuthService.resetPassword(email)
    }

    override fun checkLoginStatus(): Boolean {
        return !firebaseAuthService.getSignedInUserId().isNullOrEmpty()
    }

}