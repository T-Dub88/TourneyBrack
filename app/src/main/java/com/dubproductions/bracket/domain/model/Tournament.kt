package com.dubproductions.bracket.domain.model

import com.dubproductions.bracket.utils.status.TournamentStatus
import com.dubproductions.bracket.utils.type.TournamentType

data class Tournament(
    val name: String,
    val type: TournamentType,
    val rounds: List<Round>,
    val participants: List<Participant>,
    val status: TournamentStatus,
    val timeStarted: Long?,
    val timeEnded: Long?,
    val hostId: String
)
