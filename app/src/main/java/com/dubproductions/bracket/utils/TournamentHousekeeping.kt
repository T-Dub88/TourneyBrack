package com.dubproductions.bracket.utils

import com.dubproductions.bracket.domain.model.Participant
import kotlin.math.ceil
import kotlin.math.log2

object TournamentHousekeeping {
    fun setNumberOfRounds(participantIds: List<String>): Int {
        // participants <= 2^rounds
        return if (participantIds.isNotEmpty()) {
            val participantNth = log2(participantIds.size.toDouble())
            ceil(participantNth).toInt()
        } else {
            0
        }
    }

//    fun timeStampStart() {
//        timeStarted = Date().time
//    }
//
//    fun timeStampFinish() {
//        timeCompleted = Date().time
//    }
//
    fun sortPlayerStandings(participants: List<Participant>): List<Participant> {
        return participants.sortedWith(
            compareBy(
                { player -> player.dropped },
                { player -> -player.points },
                { player -> -player.opponentsAvgPoints },
                { player -> -player.opponentsOpponentsAvgPoints }
            )
        )
    }
}