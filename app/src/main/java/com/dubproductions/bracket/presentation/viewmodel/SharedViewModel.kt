package com.dubproductions.bracket.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.domain.model.User
import com.dubproductions.bracket.domain.repository.TournamentRepository
import com.dubproductions.bracket.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _participatingTournaments = MutableStateFlow(listOf<Tournament>())
    val participatingTournaments = _participatingTournaments.asStateFlow()

    var viewingTournamentId: String = ""
    var viewingParticipantId: String = ""

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

//    private fun updateHostingTournaments(tournamentId: String, roundId: String, matchId: String, participantId: String) {
//        val selectedParticipant = selectedTournament?.participants?.find { it.userId == participantId }
//        val selectedRound = selectedTournament?.rounds?.find { it.roundId == roundId }
//        val selectedMatch = selectedRound?.matchList?.find { it.matchId == matchId }
//    }
//
//    fun findTournament(tournamentId: String): Tournament {
//        val selectedTournament = completedTournaments.value.find { it.tournamentId == tournamentId }
//
//    }

    private fun fetchUserData() {
        userRepository.fetchUserData(
            onComplete = { userData ->
                updateUser(userData)
                if (userData.completedTournamentIds.size != completedTournaments.value.size) {
                    fetchCompletedTournaments(userData.completedTournamentIds)
                }
            }
        )
    }

    private fun fetchCompletedTournaments(tournamentIdList: List<String>) {
        for (tournamentId in tournamentIdList) {
            if (completedTournaments.value.find { it.tournamentId == tournamentId } == null) {
                viewModelScope.launch {
                    val tournament = tournamentRepository.fetchCompletedTournamentData(tournamentId)
                    updateCompletedTournaments(tournament)
                }
            }
        }
    }

}
