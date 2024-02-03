package com.dubproductions.bracket.domain.model

import com.dubproductions.bracket.utils.status.MatchStatus

data class Match(
    val id: String,
    val playerOne: Participant,
    val playerTwo: Participant,
    val winner: Participant,
    val tie: Boolean?,
    val roundNum: Int,
    val status: MatchStatus
)
