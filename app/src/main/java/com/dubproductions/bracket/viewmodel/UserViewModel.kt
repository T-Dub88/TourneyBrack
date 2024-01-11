package com.dubproductions.bracket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.data.Tournament
import com.dubproductions.bracket.data.User
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepositoryImpl
): ViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()

    private val _completedTournamentList: MutableStateFlow<MutableList<Tournament>> = MutableStateFlow(mutableListOf())
    val completedTournamentList: StateFlow<MutableList<Tournament>> = _completedTournamentList.asStateFlow()

    private val _hostingTournamentList: MutableStateFlow<MutableList<Tournament>> = MutableStateFlow(mutableListOf())
    val hostingTournamentList: StateFlow<MutableList<Tournament>> = _hostingTournamentList.asStateFlow()

    private val _participatingTournamentList: MutableStateFlow<MutableList<Tournament>> = MutableStateFlow(mutableListOf())
    val participatingTournamentList: StateFlow<MutableList<Tournament>> = _participatingTournamentList.asStateFlow()

    private var loggedIn: Boolean = false
    private var attemptedFetchUserData: Boolean = false


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
        Log.i("User", "updateUser: ${user.value}")

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
                val retrievedTournament = fetchTournament(id)
                retrievedTournament?.let {tournament ->
                    if (tournament.status == "complete") {
                        updateCompletedTournamentList(tournament)
                    } else if (hosting){
                        updateHostingTournamentList(tournament)
                    } else {
                        updateParticipatingTournamentList(tournament)
                    }
                }
            }
        }
    }

    private fun updateParticipatingTournamentList(tournament: Tournament) {
        _participatingTournamentList.update { previousTourneyList ->
            if (tournament !in previousTourneyList) {
                previousTourneyList.add(tournament)
            }
            previousTourneyList
        }
    }

    private fun updateHostingTournamentList(tournament: Tournament){
        _hostingTournamentList.update { previousTourneyList ->
            if (tournament !in previousTourneyList) {
                previousTourneyList.add(tournament)
            }
            previousTourneyList
        }
    }

    private fun updateCompletedTournamentList(tournament: Tournament) {
        _completedTournamentList.update { previousTourneyList ->
            if (tournament !in previousTourneyList) {
                previousTourneyList.add(tournament)
            }
            previousTourneyList
        }
    }

    private suspend fun fetchTournament(tournamentId: String): Tournament? {
        return viewModelScope.async {
            tournamentRepository.fetchTournamentData(tournamentId)
        }.await()
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
                    !loggedIn && user?.userId == null -> {
                        onComplete(false)
                    }
                }
            }
        }
    }

}
