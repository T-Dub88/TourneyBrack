package com.dubproductions.bracket.domain.model

import com.dubproductions.bracket.utils.status.MatchStatus
import com.dubproductions.bracket.utils.status.TournamentStatus
import java.util.Date
import kotlin.math.ceil
import kotlin.math.log2

data class Tournament(
    var name: String? = null,
    var tournamentId: String? = null,
    var type: String? = null,
    var roundIds: List<String>? = null,
    var participantIds: List<String>? = null,
    var status: String? = null,
    var timeStarted: Long? = null,
    var timeCompleted: Long? = null,
    var hostId: String? = null
)
