package com.dubproductions.bracket.utils

import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant

object ScoreUpdates {
    fun Participant.updateTiebreakers(
        participantList: List<Participant>,
        matchList: List<Match>
    ) {
        var firstTiebreaker = 0.0
        var secondTiebreaker = 0.0

        for (match in matchList) {

            val opponentId = if (match.playerOneId != userId) {
                match.playerOneId
            } else {
                match.playerTwoId
            }

            val opponent = participantList.find { opponentId == it.userId }

            opponent?.points?.let { opponentsPoints ->

                firstTiebreaker += opponentsPoints

                secondTiebreaker += if (match.winnerId == userId) {
                    opponentsPoints
                } else if (match.tie == true) {
                    (opponentsPoints * 0.5)
                } else {
                    0.0
                }

            }
        }
    }

}