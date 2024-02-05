package com.dubproductions.bracket.data.remote

import android.util.Log
import com.dubproductions.bracket.data.model.FirestoreUserData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

private const val TAG = "FirestoreService"

class FirestoreService {

    private val firestore = Firebase.firestore

    suspend fun createNewUserData(user: FirestoreUserData): Boolean {

        if (user.userId.isNullOrEmpty()) {
            return false
        }

        return try {
            firestore
                .collection("users")
                .document(user.userId!!)
                .set(user)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "createNewUserData: $e")
            false
        }
    }

}
