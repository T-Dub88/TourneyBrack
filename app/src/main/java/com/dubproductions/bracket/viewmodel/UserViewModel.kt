package com.dubproductions.bracket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.data.Tournament
import com.dubproductions.bracket.data.TournamentStatus
import com.dubproductions.bracket.data.User
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepositoryImpl
): ViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(
        User(
            email = "",
            firstName = "",
            lastName = "",
            username = ""
        )
    )
    val user: StateFlow<User> = _user.asStateFlow()

    private val _completedTournamentList: MutableStateFlow<List<Tournament>> = MutableStateFlow(listOf())
    val completedTournamentList: StateFlow<List<Tournament>> = _completedTournamentList.asStateFlow()

    private val _hostingTournamentList: MutableStateFlow<List<Tournament>> = MutableStateFlow(listOf())
    val hostingTournamentList: StateFlow<List<Tournament>> = _hostingTournamentList.asStateFlow()

    private val _participatingTournamentList: MutableStateFlow<List<Tournament>> = MutableStateFlow(mutableListOf())
    val participatingTournamentList: StateFlow<List<Tournament>> = _participatingTournamentList.asStateFlow()

    private val _viewingTournament: MutableStateFlow<Tournament> = MutableStateFlow(
        Tournament(
            name = "",
            participants = listOf(),
            status = "",
            type = ""
        )
    )
    val viewingTournament: StateFlow<Tournament> = _viewingTournament.asStateFlow()

    private var loggedIn: Boolean = false
    private var attemptedFetchUserData: Boolean = false
    var viewingTournamentId: String = ""


    init {
        Log.i("UserViewModel", "Created")
        fetchUserData { }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("UserViewModel", "Cleared")
    }

    private fun updateUser(updatedUser: User) {
        _user.update { updatedUser }

        // Update all hosting and hosted tournaments
        updatedUser.hostTournaments?.let { tournamentIdList ->
            launchTourneyListUpdate(tournamentIdList, true)
        }

        // Update all participating lists
        updatedUser.participatingTournaments?.let { tournamentIdList ->
            launchTourneyListUpdate(tournamentIdList, false)
        }
    }

    private fun launchTourneyListUpdate(
        tournamentIdList: List<String>,
        hosting: Boolean
    ) {
        for (id in tournamentIdList) {
            viewModelScope.launch {
                if (!hosting) {
                    val retrievedTournament = fetchTournament(id)
                    retrievedTournament?.let { tournament ->
                        if (tournament.status == TournamentStatus.COMPLETE.status) {
                            updateCompletedTournamentList(tournament)
                        } else {
                            updateParticipatingTournamentList(tournament)
                        }
                    }
                }
                if (hosting && tournamentRepository.tournamentListenerMap[id] == null) {
                    tournamentRepository.listenToTournament(id) {
                        it?.let {
                            updateHostingTournamentList(it)
                        }
                    }
                }
            }
        }
    }

    private fun updateParticipatingTournamentList(tournament: Tournament) {
        _participatingTournamentList.update { previousTourneyList ->
            val newList = previousTourneyList.toMutableList()
            if (tournament !in newList) {
                newList.add(tournament)
            }
            newList
        }
    }

    private fun updateHostingTournamentList(tournament: Tournament) {
        _hostingTournamentList.update { previousTourneyList ->
            val newList = previousTourneyList.toMutableList()
            val tourneyToRemove = previousTourneyList.find { it.id == tournament.id }

            tourneyToRemove?.let {
                newList.remove(it)
            }

            if (viewingTournamentId == tournament.id) {
                updateViewingTournament(tournament)
            }

            newList.add(tournament)

            newList
        }
    }

    fun removeDeletedTournamentFromFlow(id: String) {
        _hostingTournamentList.update { tourneyList ->
            val newList = tourneyList.toMutableList()
            newList.removeIf { it.id == id }
            newList
        }
    }

    fun updateViewingTournament(tournament: Tournament) {
        _viewingTournament.update {
            tournament
        }
    }

    private fun updateCompletedTournamentList(tournament: Tournament) {
        _completedTournamentList.update { previousTourneyList ->
            val newList = previousTourneyList.toMutableList()
            if (tournament !in newList) {
                newList.add(tournament)
            }
            newList
        }
    }

    private suspend fun fetchTournament(tournamentId: String): Tournament? {
        return tournamentRepository.fetchTournamentData(tournamentId)
    }

    private fun fetchUserData(onComplete: (Boolean) -> Unit) {
        attemptedFetchUserData = true
        viewModelScope.launch {
            tournamentRepository.fetchUserData { user ->
                when {
                    loggedIn && user?.userId != null -> {
                        updateUser(user)
                    }
                    !loggedIn && user?.userId != null -> {
                        updateUser(user)
                        loggedIn = true
                        onComplete(true)
                    }
                    !loggedIn -> {
                        onComplete(false)
                    }
                }
            }
        }
    }

}
