package com.dubproductions.bracket.data

import java.util.Date
import kotlin.math.ceil
import kotlin.math.log2

data class Tournament(
    var name: String = "",
    var id: String? = null,
    var type: String = "",
    var rounds: MutableList<Round>? = null,
    var participants: List<Participant> = listOf(),
    var status: String = "registering",
    var timeStarted: Long? = null,
    var timeCompleted: Long? = null,
    var hostId: String? = null
) {

    // Status strings: registering, closed, playing, complete

    fun createNextRound() {

        for (participant in participants) {
            participant.updateTiebreakers(participants)
        }

        if (rounds.isNullOrEmpty()) {
            // First round
            rounds = mutableListOf()
            val randomizedParticipantList = participants.shuffled()
            rounds!!.add(
                Round(
                    matches = generateRoundMatchList(randomizedParticipantList, 1),
                    roundNumber = 1,
                    bye = if (randomizedParticipantList.size % 2.0 != 0.0) {
                        randomizedParticipantList.last().userId
                    } else {
                        null
                    }
                )
            )

        } else {
            // after the first round
            val sortedParticipantList = sortPlayerStandings()
            val newRoundNumber = rounds!!.last().roundNumber + 1
            rounds!!.add(
                Round(
                    matches = generateRoundMatchList(sortedParticipantList, newRoundNumber),
                    roundNumber = newRoundNumber,
                    bye = if (sortedParticipantList.size % 2.0 != 0.0) {
                        sortedParticipantList.last().userId
                    } else {
                        null
                    }
                )
            )
            participants = sortedParticipantList
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
                        playerOneId = participantList[i].userId,
                        playerTwoId = participantList[j].userId,
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
                        playerOneId = participantList[i].userId,
                        playerTwoId = participantList[j].userId,
                        round = roundNumber
                    )
                )
                i += 2
                j += 2
            }
            matchList
        }
    }

    fun setNumberOfRounds(): Int {
        // participants <= 2^rounds
        return if (participants.isNotEmpty()) {
            val participantNth = log2(participants.size.toDouble())
            ceil(participantNth).toInt()
        } else {
            0
        }
    }

    fun timeStampStart() {
        timeStarted = Date().time
    }

    fun timeStampFinish() {
        timeCompleted = Date().time
    }

    fun sortPlayerStandings(): List<Participant> {
        return participants.sortedWith(
            compareBy(
                { player -> player.dropped },
                { player -> -player.points },
                { player -> -player.buchholz },
                { player -> -player.sonnebornBerger }
            )
        )
    }

}
