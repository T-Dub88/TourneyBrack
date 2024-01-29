package com.dubproductions.bracket.viewmodel

import androidx.lifecycle.ViewModel
import com.dubproductions.bracket.data.repository.TournamentRepositoryImpl
import javax.inject.Inject

class ParticipantMatchesViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepositoryImpl
): ViewModel() {
    fun declareMatchWinner(winnerId: String?) {
        // TODO: NOT YET IMPLEMENTED
    }
}
