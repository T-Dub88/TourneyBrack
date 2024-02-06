package com.dubproductions.bracket.data.repository

import com.dubproductions.bracket.data.remote.FirebaseAuthService
import com.dubproductions.bracket.data.remote.FirestoreService
import com.dubproductions.bracket.domain.model.User
import com.dubproductions.bracket.domain.repository.UserRepository

class UserRepositoryImpl(
    private val firestoreService: FirestoreService,
    private val firebaseAuthService: FirebaseAuthService
): UserRepository {

    override fun fetchUserData(onComplete: (User) -> Unit) {

        val userId = firebaseAuthService.getSignedInUserId()

        userId?.let {
            firestoreService.createUserRealtimeListener(
                userId = it,
                onComplete = { rawUserData ->

                    val user = User(
                        userId = rawUserData.userId ?: "",
                        firstName = rawUserData.firstName ?: "",
                        lastName = rawUserData.lastName ?: "",
                        email = rawUserData.email ?: "",
                        username = rawUserData.username ?: "",
                        completedTournamentIds = rawUserData.completedTournamentIds ?: listOf(),
                        hostingTournamentIds = rawUserData.hostingTournamentIds ?: listOf(),
                        participatingTournamentIds = rawUserData.participatingTournamentIds ?: listOf()
                    )

                    onComplete(user)

                }
            )
        }

    }

}