package com.dubproductions.bracket.core.data.repository

import com.dubproductions.bracket.auth.data.remote.FirebaseAuthService
import com.dubproductions.bracket.core.data.remote.FirestoreService
import com.dubproductions.bracket.core.domain.model.user.User
import com.dubproductions.bracket.core.domain.repository.UserRepository

class UserRepositoryImpl(
    private val firestoreService: FirestoreService,
    private val firebaseAuthService: FirebaseAuthService
) : UserRepository {

    override fun fetchUserData(onComplete: (User) -> Unit) {

        val userId = firebaseAuthService.getSignedInUserId()

        userId?.let {
            firestoreService.createUserRealtimeListener(
                userId = it,
                onComplete = onComplete
            )
        }

    }

    override fun fetchUserId(): String {
        return firebaseAuthService.getSignedInUserId() ?: ""
    }

}