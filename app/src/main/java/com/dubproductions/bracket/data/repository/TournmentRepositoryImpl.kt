package com.dubproductions.bracket.data.repository

import android.util.Log
import com.dubproductions.bracket.data.model.RawTournament
import com.dubproductions.bracket.data.remote.FirestoreService
import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.model.Round
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.domain.repository.TournamentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            roundIds = rawTournament.roundIds,
            participants = fetchCompletedTournamentParticipants(rawTournament.tournamentId),
            participantIds = rawTournament.participantIds,
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
                matches = fetchCompletedRoundMatches(tournamentId, rawRound.roundId),
                roundNum = rawRound.roundNum,
                byeParticipantId = rawRound.byeParticipantId
            )

            rounds.add(round)
            rounds.sortBy { -it.roundNum }
        }

        return rounds
    }

    private suspend fun fetchHostingTournamentRounds(tournamentId: String): List<Round> {
        val rounds = mutableListOf<Round>()
        val rawRounds= firestoreService.fetchRounds(tournamentId)

        for (rawRound in rawRounds) {
            val round = Round(
                roundId = rawRound.roundId,
                matchIds = rawRound.matchIds,
                roundNum = rawRound.roundNum,
                byeParticipantId = rawRound.byeParticipantId
            )

            rounds.add(round)
            rounds.sortBy { -it.roundNum }
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
        onComplete: (Tournament) -> Unit
    ) {
        firestoreService.createTournamentRealtimeListener(
            tournamentId = tournamentId,
            onComplete = {
                CoroutineScope(Dispatchers.Main).launch {
                    val tournament = withContext(Dispatchers.IO) {
                        Tournament(
                            tournamentId = it.tournamentId,
                            name = it.name,
                            type = it.type,
                            status = it.status,
                            participantIds = it.participantIds,
                            roundIds = it.roundIds,
                            timeStarted = it.timeStarted,
                            timeEnded = it.timeEnded,
                            hostId = it.hostId,
                            rounds = fetchHostingTournamentRounds(it.tournamentId)
                        )
                    }
                    onComplete(tournament)
                }
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

    override fun listenToMatch(
        tournamentId: String,
        roundId: String,
        matchId: String,
        onComplete: (Match) -> Unit
    ) {
        firestoreService.createMatchRealtimeListener(
            tournamentId = tournamentId,
            roundId = roundId,
            matchId = matchId,
            onComplete = onComplete
        )
    }

    override suspend fun updateTournamentStatus(tournamentId: String, status: String) {
        firestoreService.updateTournamentStatus(
            id = tournamentId,
            status = status
        )
    }

    override suspend fun deleteTournament(tournament: Tournament): Boolean {
        firestoreService.removeTournamentListener(tournament.tournamentId)
        return firestoreService.removeTournamentFromDatabase(tournament)
    }

    override suspend fun deleteParticipant(
        tournamentId: String,
        participantId: String
    ): Boolean {
        return firestoreService.removeParticipantFromTournament(tournamentId, participantId)
    }

    override suspend fun deleteRound(tournamentId: String, roundId: String): Boolean {
        return firestoreService.removeRoundFromTournament(tournamentId, roundId)
    }

    override suspend fun deleteMatch(
        tournamentId: String,
        roundId: String,
        matchId: String
    ): Boolean {
        return firestoreService.removeMatchFromRound(tournamentId, roundId, matchId)
    }


//

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
