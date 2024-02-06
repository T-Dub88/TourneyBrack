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

    private val _hostingTournamentList = MutableStateFlow(listOf<Tournament>())
    val hostingTournamentList = _hostingTournamentList.asStateFlow()

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

    private fun updateHostingTournamentList(tournament: Tournament) {
        _hostingTournamentList.update { oldList ->
            val newList = oldList.toMutableList()
            val removeData = newList.find { it.tournamentId == tournament.tournamentId }

            removeData?.let {
                newList.remove(it)
            }

            newList.add(tournament)
            newList.sortBy { it.timeStarted }

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
                    updateCompletedTournaments(tournament)
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
                    }
                )
            }
        }
    }

}
