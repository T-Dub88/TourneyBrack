package com.dubproductions.bracket.domain.model

import com.dubproductions.bracket.utils.status.TournamentStatus
import com.dubproductions.bracket.utils.type.TournamentType

data class Tournament(
    val tournamentId: String = "",
    val name: String = "",
    val type: String = TournamentType.SWISS.typeString,
    val roundIds: List<String> = listOf(),
    val participantIds: List<String> = listOf(),
    val status: String = TournamentStatus.REGISTERING.statusString,
    val timeStarted: Long? = null,
    val timeEnded: Long? = null,
    val hostId: String = ""
)
