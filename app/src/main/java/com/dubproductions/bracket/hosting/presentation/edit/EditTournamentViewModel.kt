package com.dubproductions.bracket.hosting.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.bracket.core.domain.repository.TournamentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EditTournamentViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository
) : ViewModel() {

    private val _uiState= MutableStateFlow(EditTournamentState())
    val uiState: StateFlow<EditTournamentState> = _uiState.asStateFlow()

    private fun updateUIState(newUIState: EditTournamentState) {
        tournamentRepository
        _uiState.update {
            newUIState
        }
    }

    fun changeOpenedDialogState(display: Boolean) {
        val newUIState = uiState.value.copy(
            displayOpenedDialog = display
        )
        updateUIState(newUIState)
    }

    fun changeClosedDialogState(display: Boolean) {
        val newUIState = uiState.value.copy(
            displayClosedDialog = display
        )
        updateUIState(newUIState)
    }

    fun changeBracketGenerationDialogState(display: Boolean) {
        val newUIState = uiState.value.copy(
            displayBracketGenerationDialog = display
        )
        updateUIState(newUIState)
    }

    fun changeDeleteDialogState(display: Boolean) {
        val newUIState = uiState.value.copy(
            displayDeleteTournamentDialog = display
        )
        updateUIState(newUIState)
    }

    fun changeNewRoundDialogState(display: Boolean) {
        val newUIState = uiState.value.copy(
            displayCreateNewRoundDialog = display
        )
        updateUIState(newUIState)
    }

    fun changeCompleteRoundsDialogState(display: Boolean) {
        val newUIState = uiState.value.copy(
            displayCompleteRoundsDialog = display
        )
        updateUIState(newUIState)
    }

    fun changeCompleteTournamentDialogState(display: Boolean) {
        val newUIState = uiState.value.copy(
            displayCompleteTournamentDialog = display
        )
        updateUIState(newUIState)
    }

    fun changeMatchesIncompleteDialogState(display: Boolean) {
        val newUIState = uiState.value.copy(
            displayMatchesIncompleteDialog = display
        )
        updateUIState(newUIState)
    }

    fun enableDisableUIState(enable: Boolean) {
        val newUIState = uiState.value.copy(
            uiEnabled = enable
        )
        updateUIState(newUIState)
    }

    fun updateTournamentStatus(id: String, status: String) {
        viewModelScope.launch {
            tournamentRepository.updateTournamentStatus(id, status)
        }
    }

    fun startTournament(tournamentId: String) {
        val timestamp = Date().time

        viewModelScope.launch {
            tournamentRepository.startTournament(tournamentId, timestamp)
        }

    }

}
