package com.dubproductions.bracket.core.domain.model.tournament


import com.dubproductions.bracket.core.domain.model.participant.Participant
import com.dubproductions.bracket.core.domain.model.round.Round

data class Tournament(
    val tournamentId: String = "",
    val name: String = "",
    val type: String = TournamentType.SWISS.typeString,
    val rounds: List<Round> = listOf(),
    val roundIds: List<String> = listOf(),
    val participants: List<Participant> = listOf(),
    val participantIds: List<String> = listOf(),
    val status: String = TournamentStatus.REGISTERING.statusString,
    val timeStarted: Long? = null,
    val timeEnded: Long? = null,
    val hostId: String = ""
)
