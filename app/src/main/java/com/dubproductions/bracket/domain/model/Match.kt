package com.dubproductions.bracket.domain.model

import com.dubproductions.bracket.utils.status.MatchStatus

data class Match(
    val matchId: String = "",
    val playerOneId: String = "",
    val playerTwoId: String = "",
    val winnerId: String? = null,
    val tie: Boolean? = null,
    val roundNum: Int = 1,
    val status: String = MatchStatus.PENDING.statusString
)
