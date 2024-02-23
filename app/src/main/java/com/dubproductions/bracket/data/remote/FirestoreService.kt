package com.dubproductions.bracket.data.remote

import android.util.Log
import com.dubproductions.bracket.data.model.RawRound
import com.dubproductions.bracket.data.model.RawTournament
import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.domain.model.User
import com.dubproductions.bracket.utils.status.TournamentStatus
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
private const val COMPLETED_IDS = "completedTournamentIds"
private const val POINTS = "points"
private const val TIME_STARTED = "timeStarted"
private const val OA = "opponentsAveragePoints"
private const val OOA = "opponentsOpponentsAveragePoints"
private const val STATUS = "status"
private const val OPP_IDS = "opponentIds"
private const val MATCH_IDS = "matchIds"
private const val ROUND_IDS = "roundIds"
private const val DROPPED = "dropped"
private const val PARTICIPANT_IDS = "participantIds"
private const val TIME_ENDED = "timeEnded"

class FirestoreService {

    private val firestore = Firebase.firestore

    private val tournamentListeners = mutableMapOf<String, ListenerRegistration>()
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

    suspend fun fetchCompletedTournamentData(tournamentId: String): RawTournament? {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .get()
                .await()
                .toObject<RawTournament>()
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
    ): List<RawRound> {

        val roundData = mutableListOf<RawRound>()

        return try {
            val result = firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(ROUNDS)
                .get()
                .await()

            result.forEach { queryDocumentSnapshot ->
                val round = queryDocumentSnapshot.toObject<RawRound>()
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
        onComplete: (RawTournament) -> Unit
    ) {
        if (!tournamentListeners.containsKey(tournamentId)) {
            tournamentListeners[tournamentId] = firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .addSnapshotListener { value, error ->
                    Log.i(TAG, "createTournamentRealtimeListener: ${tournamentListeners.size}")
                    if (error != null) {
                        Log.e(TAG, "createTournamentRealtimeListener: $error")
                        return@addSnapshotListener
                    }

                    if (value != null && value.exists()) {
                        val tournament = value.toObject<RawTournament>()
                        tournament?.let {
                            onComplete(it)
                        }
                    }
                }
        }

    }

    fun removeTournamentListener(tournamentId: String) {
        tournamentListeners[tournamentId]?.remove()
        tournamentListeners.remove(tournamentId)
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
                   Log.i(TAG, "createParticipantRealtimeListener: ${participantListeners.size}")
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
        participantListeners.remove(participantId)
    }

    fun createMatchRealtimeListener(
        tournamentId: String,
        roundId: String,
        matchId: String,
        onComplete: (Match) -> Unit
    ) {
        if (!matchListeners.containsKey(matchId)) {
            matchListeners[matchId] = firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(ROUNDS)
                .document(roundId)
                .collection(MATCHES)
                .document(matchId)
                .addSnapshotListener { value, error ->
                    Log.i(TAG, "createMatchRealtimeListener: ${matchListeners.size}")
                    if (error != null) {
                        Log.e(TAG, "createMatchRealtimeListener: $error")
                        return@addSnapshotListener
                    }

                    if (value != null && value.exists()) {
                        val match = value.toObject<Match>()
                        match?.let {
                            onComplete(it)
                        }
                    }
                }
        }
    }

    fun removeMatchListener(matchId: String) {
        matchListeners[matchId]?.remove()
        matchListeners.remove(matchId)
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

    suspend fun addParticipantIdToTournament(tournamentId: String, participantId: String) {
        try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .update(PARTICIPANT_IDS, FieldValue.arrayUnion(participantId))
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "addParticipantIdToTournament: $e")
        }
    }

    suspend fun addTournamentData(tournament: RawTournament): Boolean {
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

    suspend fun addTournamentIdToCompletedList(userId: String, tournamentId: String): Boolean {
        return try {

            firestore
                .collection(USERS)
                .document(userId)
                .update(COMPLETED_IDS, FieldValue.arrayUnion(tournamentId))
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "addTournamentIdToCompletedList: $e")
            false
        }
    }

    suspend fun updateTournamentStatus(id: String, status: String) {
        try {
            firestore
                .collection(TOURNAMENTS)
                .document(id)
                .update(STATUS, status)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "updateTournamentStatus: $e")
        }

    }

    suspend fun removeTournamentFromDatabase(
        tournament: Tournament,
    ): Boolean {
        return try {
            removeTournamentFromUser(
                userId = tournament.hostId,
                tournamentId = tournament.tournamentId
            )

            firestore
                .collection(TOURNAMENTS)
                .document(tournament.tournamentId)
                .delete()
                .await()

            true

        } catch (e: Exception) {
            Log.e(TAG, "removeTournamentFromDatabase: $e")
            false
        }
    }

    suspend fun removeMatchFromRound(
        tournamentId: String,
        roundId: String,
        matchId: String
    ) : Boolean {
        return try {
            removeMatchListener(matchId)
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(ROUNDS)
                .document(roundId)
                .collection(MATCHES)
                .document(matchId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "removeMatchFromRound: $e")
            false
        }
    }

    suspend fun removeOpponentIdAndMatchIdFromParticipant(
        tournamentId: String,
        participantId: String,
        matchId: String,
        opponentId: String?
    ): Boolean {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(PARTICIPANTS)
                .document(participantId)
                .update(
                    MATCH_IDS, FieldValue.arrayRemove(matchId),
                    OPP_IDS, FieldValue.arrayRemove(opponentId ?: "bye")
                )
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "removeOpponentIdAndMatchIdFromParticipant: $e")
            false
        }
    }

    suspend fun removeParticipantFromTournament(
        tournamentId: String,
        participantId: String
    ): Boolean {
        return try {
            removeParticipantListener(participantId)
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(PARTICIPANTS)
                .document(participantId)
                .delete()
                .await()
            Log.i(TAG, "removed: $participantId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "removeParticipantFromTournament: $e")
            false
        }
    }

    suspend fun removeParticipantIdFromTournament(
        tournamentId: String,
        participantId: String
    ) {
        try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .update(PARTICIPANT_IDS, FieldValue.arrayRemove(participantId))
                .await()
        } catch (e: Exception){
            Log.e(TAG, "removeParticipantIdFromTournament: $e")
        }
    }

    suspend fun removeRoundFromTournament(
        tournamentId: String,
        roundId: String
    ): Boolean {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(ROUNDS)
                .document(roundId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "removeRoundFromTournament: $e")
            false
        }
    }

    suspend fun removeRoundIdFromTournament(
        tournamentId: String,
        roundId: String,
    ): Boolean {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .update(ROUND_IDS, FieldValue.arrayRemove(roundId))
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "removeRoundIdFromTournament: $e")
            false
        }
    }

    suspend fun removeTournamentFromUser(
        userId: String,
        tournamentId: String
    ): Boolean {
        return try {
            firestore
                .collection(USERS)
                .document(userId)
                .update(HOSTING_IDS, FieldValue.arrayRemove(tournamentId))
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "removeTournamentFromUser: $e")
            false
        }
    }

    suspend fun dropParticipantFromTournament(
        tournamentId: String,
        participantId: String
    ): Boolean {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(PARTICIPANTS)
                .document(participantId)
                .update(DROPPED, true)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "dropParticipantFromTournament: $e")
            false
        }
    }

    suspend fun addRoundToDatabase(
        tournamentId: String,
        rawRound: RawRound
    ): Boolean {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(ROUNDS)
                .document(rawRound.roundId)
                .set(rawRound)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "addRoundToDatabase: $e")
            false
        }
    }

    suspend fun addMatchToDatabase(
        tournamentId: String,
        roundId: String,
        match: Match
    ): Boolean {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(ROUNDS)
                .document(roundId)
                .collection(MATCHES)
                .document(match.matchId)
                .set(match)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "addMatchToDatabase: $e")
            false
        }
    }

    suspend fun addMatchIdToParticipant(
        tournamentId: String,
        matchId: String,
        participantId: String,
        opponentId: String?
    ): Boolean {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(PARTICIPANTS)
                .document(participantId)
                .update(
                    MATCH_IDS, FieldValue.arrayUnion(matchId),
                    OPP_IDS, FieldValue.arrayUnion(opponentId ?: "bye")
                )
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "addMatchIdToParticipant: $e")
            false
        }
    }

    suspend fun addRoundIdToTournament(
        roundId: String,
        tournamentId: String
    ): Boolean {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .update(ROUND_IDS, FieldValue.arrayUnion(roundId))
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "addRoundIdToTournament: $e")
            false
        }
    }

    suspend fun updateMatchResults(
        tournamentId: String,
        roundId: String,
        match: Match
    ): Boolean {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(ROUNDS)
                .document(roundId)
                .collection(MATCHES)
                .document(match.matchId)
                .set(match)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "updateMatchResults: $e")
            false
        }
    }

    suspend fun timeStampStart(
        tournamentId: String,
        timeStamp: Long
    ): Boolean {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .update(
                    TIME_STARTED, timeStamp,
                    STATUS, TournamentStatus.PLAYING.statusString
                )
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "timeStampStart: $e")
            false
        }
    }

    suspend fun timeStampEnd(
        tournamentId: String,
        timeStamp: Long
    ): Boolean {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .update(
                    TIME_ENDED, timeStamp,
                    STATUS, TournamentStatus.FINALIZED.statusString
                )
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "timeStampEnd: $e")
            false
        }
    }

    suspend fun updateParticipantPoints(
        tournamentId: String,
        participantId: String,
        earnedPoints: Double
    ): Boolean {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(PARTICIPANTS)
                .document(participantId)
                .update(POINTS, FieldValue.increment(earnedPoints))
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "updateParticipantPoints: $e")
            false
        }
    }

    suspend fun updateParticipantTiebreakers(
        tournamentId: String,
        participantId: String,
        firstTiebreaker: Double,
        secondTiebreaker: Double
    ): Boolean {
        return try {
            firestore
                .collection(TOURNAMENTS)
                .document(tournamentId)
                .collection(PARTICIPANTS)
                .document(participantId)
                .update(
                    OA, firstTiebreaker,
                    OOA, secondTiebreaker
                )
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "updateParticipantTieBreakers: $e")
            false
        }
    }

}
