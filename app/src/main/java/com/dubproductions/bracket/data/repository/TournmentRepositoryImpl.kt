package com.dubproductions.bracket.data.repository

import com.dubproductions.bracket.data.model.RawTournament
import com.dubproductions.bracket.data.remote.FirestoreService
import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.model.Round
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.domain.repository.TournamentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

private const val TAG = "Repository Implementation"

class TournamentRepositoryImpl(
    private val firestoreService: FirestoreService
): TournamentRepository {
    override suspend fun fetchCompletedTournamentData(tournamentId: String): Tournament {
        val rawTournament = firestoreService.fetchCompletedTournamentData(tournamentId)
        return rawTournament?.let {
            convertRawTournamentToCompletedTournament(it)
        } ?: Tournament()
    }

    private suspend fun convertRawTournamentToCompletedTournament(rawTournament: RawTournament): Tournament {
        return Tournament(
            tournamentId = rawTournament.tournamentId,
            name = rawTournament.name,
            type = rawTournament.type,
            rounds = fetchCompletedTournamentRounds(rawTournament.tournamentId),
            participants = fetchCompletedTournamentParticipants(rawTournament.tournamentId),
            status = rawTournament.status,
            timeStarted = rawTournament.timeStarted,
            timeEnded = rawTournament.timeEnded,
            hostId = rawTournament.hostId
        )
    }

    private suspend fun fetchCompletedTournamentParticipants(tournamentId: String): List<Participant> {
        return firestoreService.fetchParticipants(tournamentId)
    }

    private suspend fun fetchCompletedTournamentRounds(tournamentId: String): List<Round> {

        val rounds = mutableListOf<Round>()
        val rawRounds= firestoreService.fetchRounds(tournamentId)

        for (rawRound in rawRounds) {
            val round = Round(
                roundId = rawRound.roundId,
                match = fetchCompletedRoundMatches(tournamentId, rawRound.roundId),
                roundNum = rawRound.roundNum,
                byeParticipantId = rawRound.byeParticipantId
            )

            rounds.add(round)
        }

        return rounds
    }

    private suspend fun fetchCompletedRoundMatches(
        tournamentId: String,
        roundId: String
    ): List<Match> {
        return firestoreService.fetchMatches(tournamentId, roundId)
    }

    override fun fetchHostingTournamentData(
        tournamentId: String,
        onComplete: (
            Tournament,
            participantIds: List<String>,
            roundIds: List<String>
        ) -> Unit
    ) {
        firestoreService.createTournamentRealtimeListener(
            tournamentId = tournamentId,
            onComplete = {
                val tournament = Tournament(
                        tournamentId = it.tournamentId,
                        name = it.name,
                        type = it.type,
                        status = it.status,
                        timeStarted = it.timeStarted,
                        timeEnded = it.timeEnded,
                        hostId = it.hostId,
                        rounds = listOf(),
                        participants = listOf(),
                    )

                onComplete(
                    tournament,
                    it.participantIds,
                    it.roundIds
                )

            }
        )
    }

    override suspend fun addParticipantData(tournamentId: String, participant: Participant) {
        firestoreService.addParticipantData(tournamentId, participant)
    }

    override suspend fun createTournament(tournament: RawTournament): Boolean {
        val addToHost = firestoreService
            .addTournamentIdToHostingList(
                userId = tournament.hostId,
                tournamentId = tournament.tournamentId
            )
        return if (addToHost) {
            firestoreService.addTournamentData(tournament)
        } else {
            false
        }
    }

    override fun listenToParticipant(
        tournamentId: String,
        participantId: String,
        onComplete: (Participant) -> Unit
    ) {
        firestoreService.createParticipantRealtimeListener(
            tournamentId = tournamentId,
            participantId = participantId,
            onComplete = onComplete
        )
    }

    override suspend fun updateTournamentStatus(tournamentId: String, status: String) {
        firestoreService.updateTournamentStatus(
            id = tournamentId,
            status = status
        )
    }

//
//    val tournamentListenerMap: MutableMap<String, ListenerRegistration> = mutableMapOf()
//
//    override suspend fun registerUser(
//        email: String,
//        password: String,
//        username: String,
//        firstName: String,
//        lastName: String,
//    ): Boolean {
//         return try {
//             val taskResult = auth
//                .createUserWithEmailAndPassword(email, password)
//                .await()
//
//             val creationResult = taskResult
//                 .user?.uid?.let { userId ->
//                     createUserData(
//                         userData = FirestoreUserData(
//                             username = username,
//                             userId = userId,
//                             email = email,
//                             firstName = firstName,
//                             lastName = lastName
//                         )
//                     )
//                 }
//
//             if (creationResult == true) {
//                 true
//             } else {
//                 deleteUserSignup()
//                 false
//             }
//
//        } catch (e: Exception) {
//             Log.e(TAG, "registerUser: $e")
//             false
//        }
//    }
//
//    override suspend fun createUserData(
//        userData: FirestoreUserData
//    ): Boolean {
//        return try {
//            firestore
//                .collection("Users")
//                .document(userData.userId!!)
//                .set(userData)
//                .await()
//            true
//        } catch (e: Exception) {
//            Log.e(TAG, "createUserData: $e")
//            false
//        }
//
//    }
//
//    override fun deleteUserSignup() {
//        auth.currentUser?.delete()
//    }
//
//    override suspend fun signInUser(
//        email: String,
//        password: String
//    ): Boolean {
//        return try {
//            auth
//                .signInWithEmailAndPassword(email, password)
//                .await()
//            true
//        } catch (e: Exception) {
//            Log.e(TAG, "signInUser: $e")
//            false
//        }
//
//    }
//
//    override suspend fun resetPassword(email: String): Boolean {
//        return try {
//            auth
//                .sendPasswordResetEmail(email)
//                .await()
//            true
//        } catch (e: Exception) {
//            Log.e(TAG, "resetPassword: $e")
//            false
//        }
//    }
//
//    override fun checkLoginStatus(): Boolean {
//        return auth.currentUser != null
//    }
//
//    override fun fetchUserData(
//        onComplete: (FirestoreUserData?) -> Unit
//    ) {
//        auth.currentUser?.uid?.let { userId ->
//            firestore
//                .collection("Users")
//                .document(userId)
//                .addSnapshotListener { value, error ->
//                    if (error != null) {
//                        Log.e(TAG, "fetchUserData: ${error.message}")
//                        onComplete(null)
//                        return@addSnapshotListener
//                    }
//                    if (value != null && value.exists()) {
//                        val user = value.toObject<FirestoreUserData>()
//                        onComplete(user)
//                    } else {
//                        onComplete(null)
//                    }
//                }
//        }
//    }
//
//    override suspend fun fetchTournamentData(tournamentId: String): FirestoreTournamentData? {
//        return try {
//            firestore
//                .collection("Tournaments")
//                .document(tournamentId)
//                .get()
//                .await()
//                .toObject<FirestoreTournamentData>()
//        } catch (e: Exception) {
//            Log.e(TAG, "fetchTournamentData: $e")
//            null
//        }
//    }
//
//    override suspend fun createTournament(tournament: FirestoreTournamentData): Boolean {
//        val createdTournamentRef = firestore.collection("Tournaments").document()
//        tournament.tournamentId = createdTournamentRef.id
//        tournament.hostId = auth.currentUser?.uid
//
//        return try {
//            firestore
//                .collection("Tournaments")
//                .document(tournament.tournamentId!!)
//                .set(tournament)
//                .await()
//            if (!tournament.tournamentId.isNullOrEmpty() && !tournament.hostId.isNullOrEmpty()) {
//                addTournamentIdToHost(
//                    tournamentId = tournament.tournamentId!!,
//                    userId = tournament.hostId!!
//                )
//            } else {
//                false
//            }
//
//        } catch (e: Exception) {
//            Log.e(TAG, "createTournament: $e")
//            false
//        }
//    }
//
//    override suspend fun addTournamentIdToHost(
//        tournamentId: String,
//        userId: String
//    ): Boolean {
//        return try {
//            firestore
//                .collection("Users")
//                .document(userId)
//                .update(
//                    "hostTournaments",
//                    FieldValue.arrayUnion(tournamentId)
//                )
//                .await()
//            true
//        } catch (e: Exception) {
//            Log.e(TAG, "addTournamentIdToHost: $e")
//            removeTournamentFromDatabase(tournamentId, null)
//            false
//        }
//    }
//
//    override suspend fun removeTournamentFromDatabase(
//        tournamentId: String,
//        userId: String?
//    ): Boolean {
//        return try {
//            firestore
//                .collection("Tournaments")
//                .document(tournamentId)
//                .delete()
//                .await()
//            userId?.let {
//                removeTournamentFromUser(
//                    userId = it,
//                    tournamentId = tournamentId
//                )
//            }
//            true
//        } catch (e: Exception) {
//            Log.e(TAG, "removeTournamentFromDatabase: $e")
//            false
//        }
//    }
//
//    override suspend fun removeTournamentFromUser(
//        userId: String,
//        tournamentId: String
//    ): Boolean {
//        return try {
//            firestore
//                .collection("Users")
//                .document(userId)
//                .update("hostTournaments", FieldValue.arrayRemove(tournamentId))
//                .await()
//            true
//        } catch (e: Exception) {
//            Log.e(TAG, "removeTournamentFromUser: $e")
//            false
//        }
//    }
//
//    override fun listenToTournament(
//        tournamentId: String,
//        onComplete: (FirestoreTournamentData?) -> Unit
//    ) {
//        val listener = firestore
//            .collection("Tournaments")
//            .document(tournamentId)
//            .addSnapshotListener { value, error ->
//                if (error != null) {
//                    Log.e(TAG, "listenToTournament: ${error.message}")
//                    onComplete(null)
//                    return@addSnapshotListener
//                }
//                if (value != null && value.exists()) {
//                    val tournament = value.toObject<FirestoreTournamentData>()
//                    onComplete(tournament)
//                } else {
//                    onComplete(null)
//                }
//            }
//        tournamentListenerMap[tournamentId] = listener
//    }
//
//    override fun removeTournamentListener(tournamentId: String) {
//        tournamentListenerMap[tournamentId]?.remove()
//        tournamentListenerMap.remove(tournamentId)
//    }
//

//
//    override suspend fun updateTournamentRounds(id: String, rounds: MutableList<FirestoreRoundData>) {
//        try {
//            firestore
//                .collection("Tournaments")
//                .document(id)
//                .update("rounds", rounds)
//                .await()
//        } catch (e: Exception) {
//            Log.e(TAG, "updateTournamentRounds: $e")
//        }
//    }
//
//    override suspend fun updateParticipantList(id: String, participants: List<FirestoreParticipantData>) {
//        try {
//            firestore
//                .collection("Tournaments")
//                .document(id)
//                .update("participants", participants)
//                .await()
//        } catch (e: Exception){
//            Log.e(TAG, "updateParticipantList: $e")
//        }
//    }
//
//    override suspend fun addParticipant(
//        tournamentId: String,
//        participant: FirestoreParticipantData
//    ) {
//        try {
//            firestore
//                .collection("Tournaments")
//                .document(tournamentId)
//                .update("participants", FieldValue.arrayUnion(participant))
//                .await()
//        } catch (e: Exception) {
//            Log.e(TAG, "addParticipant: $e")
//        }
//    }
//
//    override suspend fun removeParticipant(
//        tournamentId: String,
//        participant: FirestoreParticipantData
//    ) {
//        try {
//            firestore
//                .collection("Tournaments")
//                .document(tournamentId)
//                .update(
//                    "participants",
//                    FieldValue.arrayRemove(participant)
//                )
//                .await()
//        } catch (e: Exception) {
//            Log.e(TAG, "removeParticipant: $e")
//        }
//    }
//
//    override suspend fun dropParticipant(
//        tournamentId: String,
//        participant: FirestoreParticipantData
//    ) {
//        try {
//            firestore
//                .collection("Tournaments")
//                .document(tournamentId)
//                .update("participants", FieldValue.arrayUnion(participant))
//                .await()
//        } catch (e: Exception) {
//            Log.e(TAG, "dropParticipant: $e")
//        }
//    }
//
//    override suspend fun updateMatchResult(
//        tournamentId: String,
//        updatedRound: FirestoreRoundData
//    ) {
//        try {
//            firestore
//                .collection("Tournaments")
//                .document(tournamentId)
//                .update("rounds", FieldValue.arrayUnion(updatedRound))
//                .await()
//        } catch (e: Exception) {
//            Log.e(TAG, "updateMatchResult: $e")
//        }
//    }
//
//    suspend fun removeOldRoundData(
//        tournamentId: String,
//        oldRound: FirestoreRoundData
//    ) {
//        try {
//            firestore
//                .collection("Tournaments")
//                .document(tournamentId)
//                .update("rounds", FieldValue.arrayRemove(oldRound))
//                .await()
//        } catch (e: Exception) {
//            Log.e(TAG, "removeOldRoundData: $e")
//        }
//    }

}
