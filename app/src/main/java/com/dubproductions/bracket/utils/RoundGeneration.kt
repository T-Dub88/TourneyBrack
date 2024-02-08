package com.dubproductions.bracket.utils

import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.model.Round
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.utils.ScoreUpdates.updateTiebreakers
import com.dubproductions.bracket.utils.status.MatchStatus

object RoundGeneration {

//    fun Tournament.createNextRound(): Round {
//
//        for (participant in participants) {
//            participant.updateTiebreakers(
//                participantList = participants,
//                matchList = rounds.last().matches
//            )
//        }
//
//        if (rounds.isEmpty()) {
//            // First round
//            val newRounds = mutableListOf<Round>()
//            val randomizedParticipantList = participants.shuffled()
//
//            newRounds.add(
//                Round(
//                    matches = generateRoundMatchList(randomizedParticipantList, 1),
//                    roundNum = 1,
//                    byeParticipantId = if (randomizedParticipantList.size % 2.0 != 0.0) {
//                        randomizedParticipantList.last().userId
//                    } else {
//                        null
//                    }
//                )
//            )
//
//            for (match in rounds.last().matches) {
//
//                val playerOneIndex = randomizedParticipantList.indexOfFirst { it.userId == match.playerOneId }
//                val playerTwoIndex = randomizedParticipantList.indexOfFirst { it.userId == match.playerTwoId }
//
////                if (playerOneIndex != -1) {
////                    // TODO: ADD MATCH ID TO DATABASE PARTICIPANTS
////                    randomizedParticipantList[playerOneIndex].matchIds.add(match.matchId)
////                }
////                if (playerTwoIndex != -1) {
////                    randomizedParticipantList[playerTwoIndex].matchIds.add(match.matchId)
////                }
//            }
//
////            participants = randomizedParticipantList
//
//        } else {
//            // after the first round
//            val newRoundNumber = rounds.last().roundNum + 1
//
////            rounds.add(
////                // TODO: ADD NEW ROUND TO DATABASE
////                Round(
////                    matches = generateRoundMatchList(participants, newRoundNumber),
////                    roundNum = newRoundNumber,
////                    byeParticipantId = if (participants.size % 2.0 != 0.0) {
////                        participants.last().userId
////                    } else {
////                        null
////                    }
////                )
////            )
//
////            for (match in rounds.last().matches) {
////                val playerOneIndex = participants.indexOfFirst { it.userId == match.playerOneId }
////                val playerTwoIndex = participants.indexOfFirst { it.userId == match.playerTwoId }
////
////                participants[playerOneIndex].matchIds.add(match.matchId)
////                if (playerTwoIndex != -1) {
////                    participants[playerTwoIndex].matchIds.add(match.matchId)
////                }
////            }
////
////            participants = participants
//        }
//    }

    private fun generateRoundMatchList(participantList: List<Participant>, roundNumber: Int): List<Match> {
        var i = 0
        var j = 1
        val numberOfParticipants = participantList.size
        val matchList = mutableListOf<Match>()

        return if (numberOfParticipants % 2.0 == 0.0) {
            // Even number of participants
            while (j < numberOfParticipants) {
                matchList.add(
                    Match(
                        matchId = makeRandomString(),
                        playerOneId = participantList[i].userId,
                        playerTwoId = participantList[j].userId,
                        roundNum = roundNumber
                    )
                )
                i += 2
                j += 2
            }
            matchList
        } else {
            // Odd number of participants
            while (j < numberOfParticipants - 1) {
                matchList.add(
                    Match(
                        matchId = makeRandomString(),
                        playerOneId = participantList[i].userId,
                        playerTwoId = participantList[j].userId,
                        roundNum = roundNumber
                    )
                )
                i += 2
                j += 2
            }
            matchList.add(
                Match(
                    matchId = makeRandomString(),
                    playerOneId = participantList[i].userId,
                    playerTwoId = null,
                    roundNum = roundNumber,
                    winnerId = participantList[i].userId,
                    tie = false,
                    status = MatchStatus.COMPLETE.statusString
                )
            )
            matchList
        }
    }

    private fun makeRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (0..4).map { allowedChars.random() }.joinToString("")
    }
}