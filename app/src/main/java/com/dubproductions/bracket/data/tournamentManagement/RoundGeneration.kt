package com.dubproductions.bracket.data.tournamentManagement

import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.model.Round
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.utils.status.MatchStatus

class RoundGeneration(val tournament: Tournament) {

//    fun createNextRound() {
//
//        for (participant in tournament.participants) {
//            participant.updateTiebreakers(
//                participantList = tournament.participants,
//                matchList = tournament.rounds?.last()?.matches
//            )
//        }
//
//        if (tournament.rounds.isNullOrEmpty()) {
//            // First round
//            tournament.rounds = mutableListOf()
//            val randomizedParticipantList = participants.shuffled()
//            rounds!!.add(
//                Round(
//                    matches = generateRoundMatchList(randomizedParticipantList, 1),
//                    roundNumber = 1,
//                    bye = if (randomizedParticipantList.size % 2.0 != 0.0) {
//                        randomizedParticipantList.last().userId
//                    } else {
//                        null
//                    }
//                )
//            )
//
//            rounds!!.last().matches.forEach { match ->
//                val playerOneIndex = randomizedParticipantList.indexOfFirst { it.userId == match.playerOneId }
//                val playerTwoIndex = randomizedParticipantList.indexOfFirst { it.userId == match.playerTwoId }
//
//                if (playerOneIndex != -1) {
//                    randomizedParticipantList[playerOneIndex].matches.add(match.matchId)
//                }
//                if (playerTwoIndex != -1) {
//                    randomizedParticipantList[playerTwoIndex].matches.add(match.matchId)
//                }
//            }
//
//            participants = randomizedParticipantList
//
//        } else {
//            // after the first round
//            val sortedParticipantList = sortPlayerStandings()
//            val newRoundNumber = rounds!!.last().roundNumber + 1
//            rounds!!.add(
//                Round(
//                    matches = generateRoundMatchList(sortedParticipantList, newRoundNumber),
//                    roundNumber = newRoundNumber,
//                    bye = if (sortedParticipantList.size % 2.0 != 0.0) {
//                        sortedParticipantList.last().userId
//                    } else {
//                        null
//                    }
//                )
//            )
//
//            rounds!!.last().matches.forEach { match ->
//                val playerOneIndex = sortedParticipantList.indexOfFirst { it.userId == match.playerOneId }
//                val playerTwoIndex = sortedParticipantList.indexOfFirst { it.userId == match.playerTwoId }
//
//                sortedParticipantList[playerOneIndex].matches.add(match.matchId)
//                if (playerTwoIndex != -1) {
//                    sortedParticipantList[playerTwoIndex].matches.add(match.matchId)
//                }
//            }
//
//            participants = sortedParticipantList
//        }
//    }
//
//    private fun generateRoundMatchList(participantList: List<Participant>, roundNumber: Int): List<Match> {
//        var i = 0
//        var j = 1
//        val numberOfParticipants = participantList.size
//        val matchList = mutableListOf<Match>()
//
//        return if (numberOfParticipants % 2.0 == 0.0) {
//            // Even number of participants
//            while (j < numberOfParticipants) {
//                matchList.add(
//                    Match(
//                        matchId = makeRandomString(),
//                        playerOneId = participantList[i].userId,
//                        playerTwoId = participantList[j].userId,
//                        round = roundNumber
//                    )
//                )
//                i += 2
//                j += 2
//            }
//            matchList
//        } else {
//            // Odd number of participants
//            while (j < numberOfParticipants - 1) {
//                matchList.add(
//                    Match(
//                        matchId = makeRandomString(),
//                        playerOneId = participantList[i].userId,
//                        playerTwoId = participantList[j].userId,
//                        round = roundNumber
//                    )
//                )
//                i += 2
//                j += 2
//            }
//            matchList.add(
//                Match(
//                    matchId = makeRandomString(),
//                    playerOneId = participantList[i].userId,
//                    playerTwoId = null,
//                    round = roundNumber,
//                    winnerId = participantList[i].userId,
//                    tie = false,
//                    status = MatchStatus.COMPLETE.status
//                )
//            )
//            matchList
//        }
//    }

    private fun makeRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (0..4).map { allowedChars.random() }.joinToString("")
    }
}