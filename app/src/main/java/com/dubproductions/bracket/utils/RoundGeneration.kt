package com.dubproductions.bracket.utils

import com.dubproductions.bracket.data.model.RawRound
import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.utils.status.MatchStatus

object RoundGeneration {

    fun Tournament.createNextRound(matchList: List<Match>): RawRound {

        return RawRound(
            roundId = makeRandomString(),
            matchIds = matchList.map { it.matchId },
            roundNum = if (rounds.isEmpty()) {
                1
            } else {
                rounds.last().roundNum + 1
            },
            byeParticipantId = matchList.find { it.playerTwoId == null }?.playerOneId
        )

    }

    fun Tournament.generateRoundMatchList(): List<Match> {

        val roundNumber = rounds.size + 1
        val numberOfParticipants = participants.size
        val matchList = mutableListOf<Match>()

        val sortedParticipants = if (roundNumber == 1) {
            participants.shuffled()
        } else {
            participants
        }

        var i = 0
        var j = 1

        return if (numberOfParticipants % 2.0 == 0.0) {
            // Even number of participants
            while (j < numberOfParticipants) {
                matchList.add(
                    Match(
                        matchId = makeRandomString(),
                        playerOneId = sortedParticipants[i].userId,
                        playerTwoId = sortedParticipants[j].userId,
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
                        playerOneId = sortedParticipants[i].userId,
                        playerTwoId = sortedParticipants[j].userId,
                        roundNum = roundNumber
                    )
                )
                i += 2
                j += 2
            }
            matchList.add(
                Match(
                    matchId = makeRandomString(),
                    playerOneId = sortedParticipants[i].userId,
                    playerTwoId = null,
                    roundNum = roundNumber,
                    winnerId = sortedParticipants[i].userId,
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