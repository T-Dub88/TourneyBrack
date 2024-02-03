package com.dubproductions.bracket.domain.model

data class Round(
    val matchList: List<Match>,
    val roundNum: Int,
    val byeParticipant: Participant?
)
