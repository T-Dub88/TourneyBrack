package com.dubproductions.bracket.domain.model

import com.dubproductions.bracket.utils.status.MatchStatus

data class Match(
    val matchId: String,
    val playerOneId: String,
    val playerTwoId: String,
    val winnerId: String?,
    val tie: Boolean?,
    val roundNum: Int,
    val status: MatchStatus
)
