package com.dubproductions.bracket.core.data.repository

import com.dubproductions.bracket.core.data.remote.FirestoreService
import com.dubproductions.bracket.core.data.round.RawRound
import com.dubproductions.bracket.core.data.tournament.RawTournament
import com.dubproductions.bracket.core.domain.model.match.Match
import com.dubproductions.bracket.core.domain.model.participant.Participant
import com.dubproductions.bracket.core.domain.repository.TournamentRepository
import com.dubproductions.bracket.core.domain.model.round.Round
import com.dubproductions.bracket.core.domain.model.tournament.Tournament
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class TournamentRepositoryImpl(
    private val firestoreService: FirestoreService
) : TournamentRepository {
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
                matchIds = rawRound.matchIds,
                roundNum = rawRound.roundNum,
                byeParticipantId = rawRound.byeParticipantId
            )

            rounds.add(round)
            rounds.sortBy { it.roundNum }
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
                            rounds = fetchCompletedTournamentRounds(it.tournamentId)
                        )
                    }
                    onComplete(tournament)
                }
            }
        )
    }

    override suspend fun addParticipantData(
        tournamentId: String,
        participant: Participant,
        newTournament: Boolean
    ) {
        firestoreService.addParticipantData(tournamentId, participant)
        if (!newTournament) firestoreService.addParticipantIdToTournament(tournamentId, participant.userId)
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
        participantId: String,
        deletedTournament: Boolean
    ): Boolean {
        if (!deletedTournament) {
            firestoreService.removeParticipantIdFromTournament(tournamentId, participantId)
        }
        return firestoreService.removeParticipantFromTournament(tournamentId, participantId)
    }

    override suspend fun deleteRound(tournamentId: String, roundId: String): Boolean {
        return firestoreService.removeRoundFromTournament(tournamentId, roundId)
    }

    override suspend fun removeRoundId(tournamentId: String, roundId: String): Boolean {
        return firestoreService.removeRoundIdFromTournament(tournamentId, roundId)
    }

    override suspend fun deleteMatch(
        tournamentId: String,
        roundId: String,
        matchId: String
    ): Boolean {
        return firestoreService.removeMatchFromRound(tournamentId, roundId, matchId)
    }

    override suspend fun removeMatchIdAndOpponentIdFromParticipant(
        tournamentId: String,
        participantId: String,
        participantPointDeduction: Double,
        matchId: String,
        opponentId: String?
    ): Boolean {
        return firestoreService.removeOpponentIdAndMatchIdFromParticipant(
            tournamentId,
            participantId,
            participantPointDeduction,
            matchId,
            opponentId
        )
    }

    override suspend fun dropParticipant(
        tournamentId: String,
        participantId: String
    ): Boolean {
        return firestoreService.dropParticipantFromTournament(tournamentId, participantId)
    }

    override suspend fun addNewMatch(
        match: Match,
        tournamentId: String,
        roundId: String
    ) {
        firestoreService.addMatchToDatabase(tournamentId, roundId, match)
        firestoreService.addMatchIdToParticipant(
            tournamentId = tournamentId,
            matchId = match.matchId,
            participantId = match.playerOneId,
            opponentId = match.playerTwoId
        )
        match.playerTwoId?.let {
            firestoreService.addMatchIdToParticipant(
                tournamentId = tournamentId,
                matchId = match.matchId,
                participantId = it,
                opponentId = match.playerOneId
            )
        }
    }

    override suspend fun addNewRound(
        round: RawRound,
        tournamentId: String
    ): Boolean {
        return firestoreService.addRoundToDatabase(tournamentId, round)
    }

    override suspend fun addRoundIdToTournament(
        roundId: String,
        tournamentId: String
    ): Boolean {
        return firestoreService.addRoundIdToTournament(roundId, tournamentId)
    }

    override suspend fun addMatchResults(
        tournamentId: String,
        roundId: String,
        match: Match
    ): Boolean {
        return firestoreService.updateMatchResults(tournamentId, roundId, match)
    }

    override suspend fun startTournament(
        tournamentId: String,
        timestamp: Long
    ): Boolean {
        return firestoreService.timeStampStart(tournamentId, timestamp)
    }

    override suspend fun updateParticipantPoints(
        tournamentId: String,
        participantId: String,
        earnedPoints: Double
    ): Boolean {
        return firestoreService.updateParticipantPoints(
            tournamentId,
            participantId,
            earnedPoints
        )
    }

    override suspend fun updateTiebreakers(
        tournamentId: String,
        participantId: String,
        firstTiebreaker: Double,
        secondTiebreaker: Double
    ): Boolean {
        return firestoreService.updateParticipantTiebreakers(
            tournamentId,
            participantId,
            firstTiebreaker,
            secondTiebreaker
        )
    }

    override suspend fun completeTournament(tournament: Tournament) {
        // Remove all associated real time listeners for this tournament
        firestoreService.removeTournamentListener(tournament.tournamentId)

        for (participant in tournament.participants) {
            firestoreService.removeParticipantListener(participant.userId)
        }

        for (round in tournament.rounds) {
            for (match in round.matches) {
                firestoreService.removeMatchListener(match.matchId)
            }
        }

        // Update status in tournament
        firestoreService.timeStampEnd(
            tournament.tournamentId,
            Date().time
        )

        // Remove tournament id from user hosting list
        firestoreService.removeTournamentFromUser(
            tournament.hostId,
            tournament.tournamentId
        )

        // Add id to completed list
        firestoreService.addTournamentIdToCompletedList(
            tournament.hostId,
            tournament.tournamentId
        )

    }

}
