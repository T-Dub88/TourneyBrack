package com.dubproductions.bracket.data.tournamentManagement

import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant

class ScoreUpdates(
    val participant: Participant
) {

//    fun updateTiebreakers(
//        participantList: List<Participant>,
//        matchList: List<Match>?
//    ) {
//        buchholz = 0.0
//        sonnebornBerger = 0.0
//
//        for (matchId in matches) {
//
//            val match = matchList?.find { matchId == it.matchId }
//
//            val opponentId = if (match?.playerOneId != userId) {
//                match?.playerOneId
//            } else {
//                match.playerTwoId
//            }
//
//            val opponent = participantList.find { opponentId == it.userId }
//
//            opponent?.points?.let { opponentsPoints ->
//
//                buchholz += opponentsPoints
//
//                sonnebornBerger += if (match?.winnerId == userId) {
//                    opponentsPoints
//                } else if (match?.tie == true) {
//                    (opponentsPoints * 0.5)
//                } else {
//                    0.0
//                }
//
//            }
//        }
//    }

}