package com.dubproductions.bracket.core.domain.model.match

data class Match(
    val matchId: String = "",
    val playerOneId: String = "",
    val playerTwoId: String? = null,
    val winnerId: String? = null,
    val tie: Boolean? = null,
    val roundNum: Int = 1,
    val status: String = MatchStatus.PENDING.statusString
)
