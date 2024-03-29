package com.dubproductions.bracket.core.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.core.domain.model.match.Match
import com.dubproductions.bracket.core.domain.model.participant.Participant
import com.dubproductions.bracket.core.domain.model.tournament.Tournament
import com.dubproductions.bracket.core.domain.model.tournament.TournamentStatus
import com.dubproductions.bracket.core.domain.model.user.User
import com.dubproductions.bracket.core.domain.repository.TournamentRepository
import com.dubproductions.bracket.core.domain.repository.UserRepository
import com.dubproductions.bracket.core.utils.TournamentHousekeeping.setNumberOfRounds
import com.dubproductions.bracket.core.utils.TournamentHousekeeping.sortPlayerStandings
import com.dubproductions.bracket.hosting.utils.RoundGeneration.createNextRound
import com.dubproductions.bracket.hosting.utils.RoundGeneration.generateRoundMatchList
import com.dubproductions.bracket.hosting.utils.ScoreUpdates.updateFirstTieBreaker
import com.dubproductions.bracket.hosting.utils.ScoreUpdates.updateSecondTieBreaker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _user = MutableStateFlow(
        User(
            userId = "",
            username = "",
            firstName = "",
            lastName = "",
            email = "",
            hostingTournamentIds = listOf(),
            participatingTournamentIds = listOf(),
            completedTournamentIds = listOf()
        )
    )
    val user = _user.asStateFlow()

    private val _completedTournaments = MutableStateFlow(listOf<Tournament>())
    val completedTournaments = _completedTournaments.asStateFlow()

    private val _hostingTournamentList = MutableStateFlow(listOf<Tournament>())
    val hostingTournamentList = _hostingTournamentList.asStateFlow()

    var viewingTournamentId = ""
    var viewingParticipantId = ""

    init {
        fetchUserData()
    }

    private fun updateUser(updatedUser: User) {
        _user.update {
            updatedUser
        }
    }

    private fun updateCompletedTournaments(tournament: Tournament) {
        _completedTournaments.update { oldList ->
            val newList = oldList.toMutableList()
            newList.add(tournament)
            newList.sortBy { it.timeEnded }
            newList
        }
    }

    private fun updateHostingTournamentList(tournament: Tournament) {
        _hostingTournamentList.update { oldList ->
            val newList = oldList.toMutableList()
            val oldData = newList.find { it.tournamentId == tournament.tournamentId }

            if (oldData != null) {
                val newData = oldData.copy(
                    tournamentId = tournament.tournamentId,
                    name = tournament.name,
                    type = tournament.type,
                    roundIds = tournament.roundIds,
                    rounds = tournament.rounds,
                    participantIds = tournament.participantIds,
                    status = tournament.status,
                    timeStarted = tournament.timeStarted,
                    timeEnded = tournament.timeEnded,
                    hostId = tournament.hostId
                )
                newList.remove(oldData)
                newList.add(newData)
            } else {
                newList.add(tournament)
            }

            newList.sortBy { it.timeStarted }

            newList

        }
    }

    private fun updateHostingTournamentParticipantsList(
        tournamentId: String,
        participant: Participant
    ) {
        _hostingTournamentList.update { oldTournamentList ->

            val oldData = oldTournamentList.find { it.tournamentId == tournamentId }
            val oldParticipantList = oldData?.participants
            val oldParticipant = oldParticipantList?.find { it.userId == participant.userId }

            val newParticipantList = oldParticipantList?.toMutableList()
            val newTournamentList = oldTournamentList.toMutableList()

            if (newParticipantList != null) {

                if (oldParticipant != null) {
                    newParticipantList.remove(oldParticipant)
                }

                newParticipantList.add(participant)

                val sortedList = newParticipantList.sortPlayerStandings()

                val newData = oldData.copy(
                    participants = sortedList
                )

                newTournamentList.remove(oldData)
                newTournamentList.add(newData)
                newTournamentList.sortBy { it.timeStarted }

            }

            newTournamentList

        }

    }

    private fun updateHostingTournamentRoundMatches(
        tournamentId: String,
        roundId: String,
        match: Match
    ) {
        _hostingTournamentList.update { oldTournamentList ->

            val oldTournament = oldTournamentList.find { it.tournamentId == tournamentId }
            val oldRoundsList = oldTournament?.rounds
            val oldRound = oldRoundsList?.find { it.roundId == roundId }
            val oldMatchList = oldRound?.matches
            val oldMatch = oldMatchList?.find { it.matchId == match.matchId }

            val newTournamentList = oldTournamentList.toMutableList()
            val newMatchList = oldMatchList?.toMutableList()
            val newRoundsList = oldRoundsList?.toMutableList()

            if (newMatchList != null && newRoundsList != null) {

                if (oldMatch != null) {
                    newMatchList.remove(oldMatch)
                }

                newMatchList.add(match)

                val newRound = oldRound.copy(
                    matches = newMatchList
                )
                newRoundsList.remove(oldRound)
                newRoundsList.add(newRound)
                newRoundsList.sortBy { it.roundNum }

                val newTournament = oldTournament.copy(
                    rounds = newRoundsList
                )
                newTournamentList.remove(oldTournament)
                newTournamentList.add(newTournament)
                newTournamentList.sortBy { it.timeStarted }

            }

            newTournamentList

        }
    }

    fun removePlayerFromTournamentFlow(
        participant: Participant
    ) {
        _hostingTournamentList.update { oldList ->

            val newList = oldList.toMutableList()
            val tournament = newList.find { it.tournamentId == viewingTournamentId }
            val newParticipantIdList = tournament?.participantIds?.toMutableList()
            val newParticipants = tournament?.participants?.toMutableList()

            if (!newParticipants.isNullOrEmpty() && !newParticipantIdList.isNullOrEmpty()) {
                newParticipants.remove(participant)
                newParticipantIdList.remove(participant.userId)

                val newTournament = tournament.copy(
                    participantIds = newParticipantIdList,
                    participants = newParticipants
                )

                newList.remove(tournament)
                newList.add(newTournament)
                newList.sortBy { it.timeStarted }

            }

            newList

        }

    }

    private fun fetchUserData() {
        viewModelScope.launch {
            userRepository.fetchUserData(
                onComplete = { userData ->
                    updateUser(userData)
                    if (userData.completedTournamentIds.size != completedTournaments.value.size) {
                        fetchCompletedTournaments(userData.completedTournamentIds)
                    }

                    fetchHostingTournaments(userData.hostingTournamentIds)

                }
            )
        }
    }

    private fun fetchCompletedTournaments(tournamentIdList: List<String>) {
        for (tournamentId in tournamentIdList) {
            if (completedTournaments.value.find { it.tournamentId == tournamentId } == null) {
                viewModelScope.launch {
                    val tournament = tournamentRepository.fetchCompletedTournamentData(tournamentId)
                    if (tournament !in completedTournaments.value) {
                        updateCompletedTournaments(tournament)
                    }
                }
            }
        }
    }

    private fun fetchHostingTournaments(tournamentIdList: List<String>) {
        for (tournamentId in tournamentIdList) {
            viewModelScope.launch {
                tournamentRepository.fetchHostingTournamentData(
                    tournamentId = tournamentId,
                    onComplete = { tournament ->
                        updateHostingTournamentList(tournament)
                        fetchParticipants(tournamentId, tournament.participantIds)
                        for (round in tournament.rounds) {
                            fetchMatches(
                                tournamentId = tournamentId,
                                roundId = round.roundId,
                                matchIds = round.matchIds
                            )
                        }
                    }
                )
            }
        }
    }

    private fun fetchParticipants(tournamentId: String, participantIds: List<String>) {
        for (id in participantIds) {
            viewModelScope.launch {
                tournamentRepository.listenToParticipant(
                    tournamentId = tournamentId,
                    participantId = id,
                    onComplete = { participant ->
                        updateHostingTournamentParticipantsList(tournamentId, participant)
                    }
                )
            }
        }
    }

    private fun fetchMatches(
        tournamentId: String,
        roundId: String,
        matchIds: List<String>
    ) {
        for (id in matchIds) {
            viewModelScope.launch {
                tournamentRepository.listenToMatch(
                    tournamentId = tournamentId,
                    roundId = roundId,
                    matchId = id,
                    onComplete = { match ->
                        updateHostingTournamentRoundMatches(
                            tournamentId = tournamentId,
                            match = match,
                            roundId = roundId
                        )
                    }
                )
            }
        }
    }

    private fun removeDeletedTournamentFromFlow(tournamentId: String) {
        _hostingTournamentList.update { oldList ->
            val newList = oldList.toMutableList()
            newList.remove(newList.find { it.tournamentId == tournamentId })
            newList
        }
    }

    fun deleteTournament(
        tournament: Tournament
    ) {

        viewModelScope.launch {
            launch {
                tournamentRepository.deleteTournament(tournament = tournament)
            }

            for (roundId in tournament.roundIds) {
                launch {
                    tournamentRepository.deleteRound(tournament.tournamentId, roundId)
                }
            }

            for (participantId in tournament.participantIds) {
                launch {
                    tournamentRepository.deleteParticipant(
                        tournament.tournamentId,
                        participantId,
                        true
                    )
                }
            }

            for (round in tournament.rounds) {
                for (matchId in round.matchIds) {
                    launch {
                        tournamentRepository.deleteMatch(
                            tournamentId = tournament.tournamentId,
                            roundId = round.roundId,
                            matchId = matchId
                        )
                    }
                }
            }

        }

        removeDeletedTournamentFromFlow(tournament.tournamentId)

    }

    suspend fun generateBracket(tournamentId: String) {

        var tournament = hostingTournamentList.value.find { it.tournamentId == tournamentId }!!
        val jobs = mutableListOf<Deferred<Boolean>>()
        val matchJobs = mutableListOf<Deferred<Unit>>()

        if (tournament.rounds.isNotEmpty()) {
            addTiebreakerPoints(tournamentId, tournament.participants).awaitAll()
            tournament = hostingTournamentList.value.find { it.tournamentId == tournamentId }!!
        }
        val matchList = generateRoundMatchList(tournament.rounds, tournament.participants)
        val round = tournament.createNextRound(matchList)

        val roundJob = viewModelScope.async {
            tournamentRepository.addNewRound(round, tournament.tournamentId)
        }
        jobs.add(roundJob)

        for (match in matchList) {
            
            if (match.playerTwoId.isNullOrEmpty()) {
                // Add bye players points
                viewModelScope.launch {
                    tournamentRepository.updateParticipantPoints(
                        tournamentId = tournamentId,
                        participantId = match.playerOneId,
                        earnedPoints = 1.0
                    )
                }
            }

            val matchJob = viewModelScope.async {
                tournamentRepository.addNewMatch(
                    match = match,
                    tournamentId = tournament.tournamentId,
                    roundId = round.roundId
                )
            }
            matchJobs.add(matchJob)
        }

        val roundIdJob = viewModelScope.async {
            tournamentRepository.addRoundIdToTournament(round.roundId, tournament.tournamentId)
        }
        jobs.add(roundIdJob)

        matchJobs.awaitAll()
        jobs.awaitAll()

        if (tournament.rounds.size >= tournament.setNumberOfRounds() - 1) {
            tournamentRepository.updateTournamentStatus(tournamentId, TournamentStatus.COMPLETE_ROUNDS.statusString)
        }

    }

    fun addTiebreakerPoints(
        tournamentId: String,
        participants: List<Participant>
    ): List<Deferred<Boolean>> {

        val tiebreakerJobs = mutableListOf<Deferred<Boolean>>()

        // Update first tiebreak and second at same time then wait
        for (participant in participants) {
            val tiebreakerJob = viewModelScope.async {
                val firstTiebreaker = participant.updateFirstTieBreaker(participants)
                val secondTiebreaker = participant.updateSecondTieBreaker(participants)

                tournamentRepository.updateTiebreakers(
                    tournamentId = tournamentId,
                    participantId = participant.userId,
                    firstTiebreaker = firstTiebreaker,
                    secondTiebreaker = secondTiebreaker
                )

            }

            tiebreakerJobs.add(tiebreakerJob)

        }

        return tiebreakerJobs

    }

    fun completeTournament(tournament: Tournament) {
        viewModelScope.launch {
            tournamentRepository.completeTournament(tournament)
            removeDeletedTournamentFromFlow(tournament.tournamentId)
        }
    }

}
