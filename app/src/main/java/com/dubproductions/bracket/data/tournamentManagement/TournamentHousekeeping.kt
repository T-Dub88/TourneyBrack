package com.dubproductions.bracket.data.tournamentManagement

import com.dubproductions.bracket.domain.model.Participant
import java.util.Date
import kotlin.math.ceil
import kotlin.math.log2

class TournamentHousekeeping {
//    fun setNumberOfRounds(): Int {
//        // participants <= 2^rounds
//        return if (participants.isNotEmpty()) {
//            val participantNth = log2(participants.size.toDouble())
//            ceil(participantNth).toInt()
//        } else {
//            0
//        }
//    }
//
//    fun timeStampStart() {
//        timeStarted = Date().time
//    }
//
//    fun timeStampFinish() {
//        timeCompleted = Date().time
//    }
//
//    fun sortPlayerStandings(): List<Participant> {
//        return participants.sortedWith(
//            compareBy(
//                { player -> player.dropped },
//                { player -> -player.points },
//                { player -> -player.buchholz },
//                { player -> -player.sonnebornBerger }
//            )
//        )
//    }
}