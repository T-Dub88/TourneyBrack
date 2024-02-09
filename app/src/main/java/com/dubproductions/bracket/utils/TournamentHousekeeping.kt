package com.dubproductions.bracket.utils

import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.model.Tournament
import kotlin.math.ceil
import kotlin.math.log2

object TournamentHousekeeping {
    fun Tournament.setNumberOfRounds(): Int {
        // participants <= 2^rounds
        return if (participants.isNotEmpty()) {
            val participantNth = log2(participants.size.toDouble())
            ceil(participantNth).toInt()
        } else {
            0
        }
    }

    fun List<Participant>.sortPlayerStandings(): List<Participant> {
        return this.sortedWith(
            compareBy(
                { player -> player.dropped },
                { player -> -player.points },
                { player -> -player.opponentsAvgPoints },
                { player -> -player.opponentsOpponentsAvgPoints }
            )
        )
    }
}
