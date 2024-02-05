package com.dubproductions.bracket.domain.model

data class Round(
    val roundId: String,
    val matchList: List<Match>,
    val roundNum: Int,
    val byeParticipantId: String?
)
