package com.dubproductions.bracket.data.remote

import android.util.Log
import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.model.Round
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.domain.model.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

private const val TAG = "FirestoreService"
private const val USERS = "users"
private const val TOURNAMENTS = "tournaments"
private const val PARTICIPANTS = "participants"
private const val ROUNDS = "rounds"
private const val MATCHES = "matches"
private const val HOSTING_IDS = "hostingTournamentIds"

class FirestoreService {

    private val firestore = Firebase.firestore

    private val tournamentListeners = mutableMapOf<String, ListenerRegistration>()
    private val roundListeners =  mutableMapOf<String, ListenerRegistration>()
    private val matchListeners = mutableMapOf<String, ListenerRegistration>()
    private val participantListeners = mutableMapOf<String, ListenerRegistration>()
    private lateinit var userListener: ListenerRegistration

    suspend fun createNewUserData(user: User): Boolean {

        if (user.userId.isEmpty()) {
            return false
        }

        return try {
            firestore
                .collection(USERS)
                .document(user.userId)
                .set(user)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "createNewUserData: $e")
            false
        }
    }

    suspend fun fetchUserData(userId: String): User? {
        return try {
            firestore
                .collection(USERS)
                .document(userId)
                .get()
                .await()
                .toObject<User>()
        } catch (e: Exception) {
            Log.e(TAG, "fetchUserData: $e")
            null
        }
    }

    suspend fun fetchCompletedTournamentData(tournamentId: String): Tournament? {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .get()
                .await()
                .toObject<Tournament>()
        } catch (e: Exception) {
            Log.e(TAG, "fetchCompletedTournamentData: $e")
            null
        }
    }

    suspend fun fetchParticipants(
        tournamentId: String
    ): List<Participant> {

        val participantData = mutableListOf<Participant>()

        return try {
            val result = firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(PARTICIPANTS)
                .get()
                .await()

            result.forEach {
                val participant = it.toObject<Participant>()
                participantData.add(participant)
            }

            participantData

        } catch (e: Exception) {
            Log.e(TAG, "fetchParticipants: $e")
            participantData
        }
    }

    suspend fun fetchRounds(
        tournamentId: String
    ): List<Round> {

        val roundData = mutableListOf<Round>()

        return try {
            val result = firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(ROUNDS)
                .get()
                .await()

            result.forEach { queryDocumentSnapshot ->
                val round = queryDocumentSnapshot.toObject<Round>()
                roundData.add(round)
            }

            return roundData

        } catch (e: Exception) {
            Log.e(TAG, "fetchRounds: $e")
            roundData
        }
    }

    suspend fun fetchMatches(
        tournamentId: String,
        roundId: String
    ): List<Match> {

        val matchDataList = mutableListOf<Match>()

        return try {
            val result = firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(ROUNDS)
                .document(roundId)
                .collection(MATCHES)
                .get()
                .await()

            result.forEach {
                val match = it.toObject<Match>()
                matchDataList.add(match)
            }

            matchDataList

        } catch (e: Exception) {
            Log.e(TAG, "fetchMatches: $e")
            matchDataList
        }

    }

    fun createTournamentRealtimeListener(
        tournamentId: String,
        onComplete: (Tournament) -> Unit
    ) {
        if (!tournamentListeners.containsKey(tournamentId)) {
            tournamentListeners[tournamentId] = firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e(TAG, "createTournamentRealtimeListener: $error")
                        return@addSnapshotListener
                    }

                    if (value != null && value.exists()) {
                        val tournament = value.toObject<Tournament>()
                        tournament?.let {
                            onComplete(it)
                        }
                    }
                }
        }

    }

    fun removeTournamentListener(tournamentId: String) {
        tournamentListeners[tournamentId]?.remove()
    }

    fun createUserRealtimeListener(
        userId: String,
        onComplete: (User) -> Unit
    ) {
        userListener = firestore
            .collection(USERS)
            .document(userId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e(TAG, "createUserRealtimeListener: $error")
                    return@addSnapshotListener
                }

                if (value != null && value.exists()) {
                    val user = value.toObject<User>()
                    user?.let {
                        onComplete(it)
                    }
                }
            }
    }

    fun removeUserListener() {
        userListener.remove()
    }

    fun createParticipantRealtimeListener(
        tournamentId: String,
        participantId: String,
        onComplete: (Participant) -> Unit
    ) {
       if (!participantListeners.containsKey(participantId)) {
           participantListeners[participantId] = firestore
               .collection(TOURNAMENTS)
               .document(tournamentId)
               .collection(PARTICIPANTS)
               .document(participantId)
               .addSnapshotListener { value, error ->
                   if (error != null) {
                       Log.e(TAG, "createParticipantRealtimeListener: $error")
                       return@addSnapshotListener
                   }

                   if (value != null && value.exists()) {
                       val participant = value.toObject<Participant>()
                       participant?.let {
                           onComplete(it)
                       }
                   }
               }
       }
    }

    fun removeParticipantListener(participantId: String) {
        participantListeners[participantId]?.remove()
    }

    suspend fun addParticipantData(tournamentId: String, participant: Participant) {

        try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(PARTICIPANTS)
                .document(participant.userId)
                .set(participant)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "addParticipantData: $e")
        }

    }

    suspend fun addTournamentData(tournament: Tournament): Boolean {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournament.tournamentId)
                .set(tournament)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "addTournamentData: $e")
            false
        }
    }

    suspend fun addTournamentIdToHostingList(userId: String, tournamentId: String): Boolean {
        Log.i(TAG, "addTournamentIdToHostingList: $userId")
        return try {
            firestore
                .collection(USERS)
                .document(userId)
                .update(HOSTING_IDS, FieldValue.arrayUnion(tournamentId))
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "addTournamentIdToHostingList: $e")
            false
        }
    }

    suspend fun updateTournamentStatus(id: String, status: String) {
        try {
            firestore
                .collection(TOURNAMENTS)
                .document(id)
                .update("status", status)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "updateTournamentStatus: $e")
        }

    }

}
