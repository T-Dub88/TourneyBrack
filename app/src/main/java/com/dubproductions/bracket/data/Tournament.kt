package com.dubproductions.bracket.data

import kotlin.math.ceil
import kotlin.math.log2

data class Tournament(
    var id: String? = null,
    var type: String? = null,
    var rounds: MutableList<Round>? = null,
    var participants: List<Participant>? = null,
    var roundCount: Int? = null,
) {
    fun createNextRound() {

        participants?.let {
            for (participant in it) {
                participant.updateTiebreakers()
            }

            if (rounds.isNullOrEmpty()) {
                // First round
                rounds = mutableListOf()
                val randomizedParticipantList = it.shuffled()
                rounds!!.add(
                    Round(
                        matches = generateRoundMatchList(randomizedParticipantList, 1),
                        roundNumber = 1,
                        bye = if (randomizedParticipantList.size % 2.0 != 0.0) {
                            randomizedParticipantList.last()
                        } else {
                            null
                        }
                    )
                )

            } else {
                // after the first round
                val sortedParticipantList = it.sortedWith(
                    compareBy(
                        { player -> player.points },
                        { player -> player.buchholz },
                        { player -> player.sonnebornBerger }
                    )
                )
                val newRoundNumber = rounds!!.last().roundNumber!! + 1
                rounds!!.add(
                    Round(
                        matches = generateRoundMatchList(sortedParticipantList, newRoundNumber),
                        roundNumber = newRoundNumber,
                        bye = if (sortedParticipantList.size % 2.0 != 0.0) {
                            sortedParticipantList.last()
                        } else {
                            null
                        }
                    )
                )
            }
        }
    }

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
                        playerOne = participantList[i],
                        playerTwo = participantList[j],
                        round = roundNumber
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
                        playerOne = participantList[i],
                        playerTwo = participantList[j],
                        round = roundNumber
                    )
                )
                i += 2
                j += 2
            }
            matchList
        }
    }

    fun setNumberOfRounds() {
        // participants <= 2^rounds

        participants?.let {
            val participantNth = log2(it.size.toDouble())

            roundCount = ceil(participantNth).toInt()
        }

    }


}
