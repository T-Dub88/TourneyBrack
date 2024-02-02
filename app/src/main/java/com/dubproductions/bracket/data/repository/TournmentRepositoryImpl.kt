package com.dubproductions.bracket.data.repository

import android.util.Log
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.model.Round
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.domain.model.User
import com.dubproductions.bracket.domain.repository.TournamentRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

private const val TAG = "Firebase Manager"

class TournamentRepositoryImpl: TournamentRepository {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    val tournamentListenerMap: MutableMap<String, ListenerRegistration> = mutableMapOf()

    override suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String,
    ): Boolean {
         return try {
             val taskResult = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

             val creationResult = taskResult
                 .user?.uid?.let { userId ->
                     createUserData(
                         userData = User(
                             username = username,
                             userId = userId,
                             email = email,
                             firstName = firstName,
                             lastName = lastName
                         )
                     )
                 }

             if (creationResult == true) {
                 true
             } else {
                 deleteUserSignup()
                 false
             }

        } catch (e: Exception) {
             Log.e(TAG, "registerUser: $e")
             false
        }
    }

    override suspend fun createUserData(
        userData: User
    ): Boolean {
        return try {
            firestore
                .collection("Users")
                .document(userData.userId!!)
                .set(userData)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "createUserData: $e")
            false
        }

    }

    override fun deleteUserSignup() {
        auth.currentUser?.delete()
    }

    override suspend fun signInUser(
        email: String,
        password: String
    ): Boolean {
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

    override suspend fun resetPassword(email: String): Boolean {
        return try {
            auth
                .sendPasswordResetEmail(email)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "resetPassword: $e")
            false
        }
    }

    override fun checkLoginStatus(): Boolean {
        return auth.currentUser != null
    }

    override fun fetchUserData(
        onComplete: (User?) -> Unit
    ) {
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

    override suspend fun fetchTournamentData(tournamentId: String): Tournament? {
        return try {
            firestore
                .collection("Tournaments")
                .document(tournamentId)
                .get()
                .await()
                .toObject<Tournament>()
        } catch (e: Exception) {
            Log.e(TAG, "fetchTournamentData: $e")
            null
        }
    }

    override suspend fun createTournament(tournament: Tournament): Boolean {
        val createdTournamentRef = firestore.collection("Tournaments").document()
        tournament.id = createdTournamentRef.id
        tournament.hostId = auth.currentUser?.uid

        return try {
            firestore
                .collection("Tournaments")
                .document(tournament.id!!)
                .set(tournament)
                .await()
            if (!tournament.id.isNullOrEmpty() && !tournament.hostId.isNullOrEmpty()) {
                addTournamentIdToHost(
                    tournamentId = tournament.id!!,
                    userId = tournament.hostId!!
                )
            } else {
                false
            }

        } catch (e: Exception) {
            Log.e(TAG, "createTournament: $e")
            false
        }
    }

    override suspend fun addTournamentIdToHost(
        tournamentId: String,
        userId: String
    ): Boolean {
        return try {
            firestore
                .collection("Users")
                .document(userId)
                .update(
                    "hostTournaments",
                    FieldValue.arrayUnion(tournamentId)
                )
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "addTournamentIdToHost: $e")
            removeTournamentFromDatabase(tournamentId, null)
            false
        }
    }

    override suspend fun removeTournamentFromDatabase(
        tournamentId: String,
        userId: String?
    ): Boolean {
        return try {
            firestore
                .collection("Tournaments")
                .document(tournamentId)
                .delete()
                .await()
            userId?.let {
                removeTournamentFromUser(
                    userId = it,
                    tournamentId = tournamentId
                )
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "removeTournamentFromDatabase: $e")
            false
        }
    }

    override suspend fun removeTournamentFromUser(
        userId: String,
        tournamentId: String
    ): Boolean {
        return try {
            firestore
                .collection("Users")
                .document(userId)
                .update("hostTournaments", FieldValue.arrayRemove(tournamentId))
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "removeTournamentFromUser: $e")
            false
        }
    }

    override fun listenToTournament(
        tournamentId: String,
        onComplete: (Tournament?) -> Unit
    ) {
        val listener = firestore
            .collection("Tournaments")
            .document(tournamentId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e(TAG, "listenToTournament: ${error.message}")
                    onComplete(null)
                    return@addSnapshotListener
                }
                if (value != null && value.exists()) {
                    val tournament = value.toObject<Tournament>()
                    onComplete(tournament)
                } else {
                    onComplete(null)
                }
            }
        tournamentListenerMap[tournamentId] = listener
    }

    override fun removeTournamentListener(tournamentId: String) {
        tournamentListenerMap[tournamentId]?.remove()
        tournamentListenerMap.remove(tournamentId)
    }

    override suspend fun updateTournamentStatus(id: String, status: String) {
        try {
            firestore
                .collection("Tournaments")
                .document(id)
                .update("status", status)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "updateTournamentStatus: $e")
        }

    }

    override suspend fun updateTournamentRounds(id: String, rounds: MutableList<Round>) {
        try {
            firestore
                .collection("Tournaments")
                .document(id)
                .update("rounds", rounds)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "updateTournamentRounds: $e")
        }
    }

    override suspend fun updateParticipantList(id: String, participants: List<Participant>) {
        try {
            firestore
                .collection("Tournaments")
                .document(id)
                .update("participants", participants)
                .await()
        } catch (e: Exception){
            Log.e(TAG, "updateParticipantList: $e")
        }
    }

    override suspend fun addParticipant(
        tournamentId: String,
        participant: Participant
    ) {
        try {
            firestore
                .collection("Tournaments")
                .document(tournamentId)
                .update("participants", FieldValue.arrayUnion(participant))
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "addParticipant: $e")
        }
    }

    override suspend fun removeParticipant(
        tournamentId: String,
        participant: Participant
    ) {
        try {
            firestore
                .collection("Tournaments")
                .document(tournamentId)
                .update(
                    "participants",
                    FieldValue.arrayRemove(participant)
                )
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "removeParticipant: $e")
        }
    }

    override suspend fun dropParticipant(
        tournamentId: String,
        participant: Participant
    ) {
        try {
            firestore
                .collection("Tournaments")
                .document(tournamentId)
                .update("participants", FieldValue.arrayUnion(participant))
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "dropParticipant: $e")
        }
    }

    override suspend fun updateMatchResult(
        tournamentId: String,
        updatedRound: Round
    ) {
        try {
            firestore
                .collection("Tournaments")
                .document(tournamentId)
                .update("rounds", FieldValue.arrayUnion(updatedRound))
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "updateMatchResult: $e")
        }
    }

    suspend fun removeOldRoundData(
        tournamentId: String,
        oldRound: Round
    ) {
        try {
            firestore
                .collection("Tournaments")
                .document(tournamentId)
                .update("rounds", FieldValue.arrayRemove(oldRound))
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "removeOldRoundData: $e")
        }
    }

}
