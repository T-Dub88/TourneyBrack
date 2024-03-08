package com.dubproductions.bracket.hosting.utils

import android.util.Log
import com.dubproductions.bracket.core.domain.model.match.Match
import com.dubproductions.bracket.core.domain.model.participant.Participant
import com.dubproductions.bracket.core.domain.model.round.Round
import com.dubproductions.bracket.core.domain.model.tournament.Tournament
import com.dubproductions.bracket.core.data.round.RawRound
import com.dubproductions.bracket.core.domain.model.match.MatchStatus
import kotlin.math.abs

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

    fun generateRoundMatchList(
        rounds: List<Round>,
        participants: List<Participant>
    ): List<Match> {

        val roundNum = rounds.size + 1
        val matchList = mutableListOf<Match>()
        var byeMatch: Match? = null

        // Get the list of unpaired participants to manipulate
        val unpairedParticipants = if (roundNum == 1) {
            participants.shuffled().toMutableList()
        } else {
            participants.toMutableList()
        }

        val viableMatches = mutableMapOf<String, MutableList<Participant>>()

        // Dropped participants will not be paired
        unpairedParticipants.removeAll { it.dropped }

        // Participants that will try to pair
        val numOriginalParticipants = unpairedParticipants.size

        // Create the bye match to make participants even
        if (numOriginalParticipants % 2 != 0) {

            val byeParticipant = unpairedParticipants.findLast {
                "bye" !in it.opponentIds
            }

            byeParticipant?.let {
                byeMatch = Match(
                    matchId = makeRandomString(),
                    playerOneId = byeParticipant.userId,
                    playerTwoId = null,
                    winnerId = byeParticipant.userId,
                    tie = false,
                    roundNum = roundNum,
                    status = MatchStatus.COMPLETE.statusString
                )

                unpairedParticipants.remove(byeParticipant)
            }

        }

        // Creates a map containing a list of viable opponents for each participant
        for (participant in unpairedParticipants) {

            viableMatches[participant.userId] = unpairedParticipants.filter {
                it.userId != participant.userId && it.userId !in participant.opponentIds
            }.sortedWith(
                compareBy(
                    { abs(it.points - participant.points) },
                    { abs(it.opponentsAveragePoints - participant.opponentsAveragePoints) },
                    { abs(it.opponentsOpponentsAveragePoints - participant.opponentsOpponentsAveragePoints) }
                )
            ).toMutableList()

            Log.i("Viable Matches", viableMatches[participant.userId].toString())

        }

        // Sort list so that adjacent participants haven't played
        while (viableMatches.isNotEmpty()) {

            val playerOneId = viableMatches.minByOrNull {
                it.value.size
            }?.key ?: break

            val playerTwoId = viableMatches[playerOneId]?.first()?.userId!!

            val match = Match(
                matchId = makeRandomString(),
                playerOneId = playerOneId,
                playerTwoId = playerTwoId,
                roundNum = roundNum,
                status = MatchStatus.PENDING.statusString
            )

            viableMatches.remove(playerOneId)
            viableMatches.remove(playerTwoId)

            for (viableOpponents in viableMatches.values) {
                viableOpponents.removeAll {
                    it.userId == playerOneId || it.userId == playerTwoId
                }
            }

            matchList.add(match)

        }

        // Add bye match to the end of the list if it exists
        byeMatch?.let {
            matchList.add(it)
        }

        return matchList

    }

    private fun makeRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (0..4).map { allowedChars.random() }.joinToString("")
    }
}