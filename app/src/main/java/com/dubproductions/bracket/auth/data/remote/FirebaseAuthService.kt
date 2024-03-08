package com.dubproductions.bracket.auth.data.remote

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

private const val TAG = "Auth"

class FirebaseAuthService {

    private val auth = Firebase.auth

    suspend fun registerUser(email: String, password: String): Boolean {
        return try {
            auth
                .createUserWithEmailAndPassword(email, password)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "registerUser: $e")
            false
        }
    }

    suspend fun signInUser(email: String, password: String): Boolean {
        return try {
            auth
                .signInWithEmailAndPassword(email, password)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "signInUser: $e")
            false
        }
    }

    suspend fun deleteUserAccount(): Boolean {
        return try {
            auth.currentUser?.delete()?.await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "deleteUserAccount: $e")
            false
        }
    }

    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "resetPassword: $e")
            false
        }
    }

    fun getSignedInUserId(): String? {
        return auth.currentUser?.uid
    }

}