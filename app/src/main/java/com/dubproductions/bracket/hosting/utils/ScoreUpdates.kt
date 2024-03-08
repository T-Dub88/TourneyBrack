package com.dubproductions.bracket.hosting.utils

import com.dubproductions.bracket.core.domain.model.participant.Participant

object ScoreUpdates {

    fun Participant.updateFirstTieBreaker(
        participants: List<Participant>
    ): Double {
        var opponentsTotalPoints = 0.0

        for (opponentId in opponentIds) {
            val opponent = participants.find { it.userId == opponentId }
            opponent?.let {
                opponentsTotalPoints += it.points
            }

        }

        return opponentsTotalPoints / opponentIds.size

    }

    fun Participant.updateSecondTieBreaker(
        participants: List<Participant>
    ): Double {

        var opponentsOpponentsTotalPoints = 0.0
        val divisor = opponentIds.size * opponentIds.size

        for (opponentId in opponentIds) {
            val opponent = participants.find { it.userId == opponentId }
            opponent?.let { opponentExists ->
                for (opponentsOpponentId in opponentExists.opponentIds) {
                    val opponentsOpponent = participants.find { it.userId == opponentsOpponentId }
                    opponentsOpponent?.let {
                        opponentsOpponentsTotalPoints += it.points
                    }
                }
            }
        }

        return opponentsOpponentsTotalPoints / divisor

    }

}