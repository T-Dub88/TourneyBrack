package com.dubproductions.bracket.domain.model

import com.dubproductions.bracket.utils.status.MatchStatus

data class Match(
    val matchId: String? = null,
    val playerOneId: String? = null,
    val playerTwoId: String? = null,
    var winnerId: String? = null,
    var tie: Boolean? = null,
    val round: Int? = null,
    var status: String? = null
)
