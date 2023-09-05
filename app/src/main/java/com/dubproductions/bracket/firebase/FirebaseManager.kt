package com.dubproductions.bracket.firebase

import android.util.Log
import com.dubproductions.bracket.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

private const val TAG = "Firebase Manager"

class FirebaseManager {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    fun registerUser(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String,
        onComplete: (success: Boolean) -> Unit
    ) {
        auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.user?.uid?.let { userId ->
                        createUserData(
                            userData = User(
                                userId = userId,
                                email = email,
                                username = username,
                                firstName = firstName,
                                lastName = lastName
                            )
                        ) { success ->
                            if (!success) {
                                deleteAuthUser()
                            }
                            onComplete(success)
                        }
                    }
                } else {
                    Log.e(TAG, "registerUser: ${task.exception}")
                    onComplete(false)
                }
            }
    }

    private fun createUserData(userData: User, onComplete: (success: Boolean) -> Unit) {
        userData.userId?.let { userId ->
            firestore
                .collection("Users")
                .document(userId)
                .set(userData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onComplete(true)
                    } else {
                        Log.e(TAG, "createUserData: ${task.exception}")
                        onComplete(false)
                    }
                }
        }
    }

    private fun deleteAuthUser() {
        auth.currentUser?.delete()
    }

    fun signInUser(email: String, password: String, onComplete: (success: Boolean) -> Unit) {
        auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true)
                } else {
                    Log.e(TAG, "signInUser: ${task.exception}")
                    onComplete(false)
                }
            }
    }

    fun fetchUserData(onComplete: (user: User?) -> Unit) {
        auth.currentUser?.uid?.let { userId ->
            firestore
                .collection("Users")
                .document(userId)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e(TAG, "fetchUserData: ${error.message}")
                        onComplete(null)
                        return@addSnapshotListener
                    }
                    if (value != null && value.exists()) {
                        val user = value.toObject<User>()
                        onComplete(user)
                    } else {
                        onComplete(null)
                    }
                }
        }
    }

    fun resetUserPassword(email: String, onComplete: (success: Boolean) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            onComplete(it.isSuccessful)
        }
    }

}
