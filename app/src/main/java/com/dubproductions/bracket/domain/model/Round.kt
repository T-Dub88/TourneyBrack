package com.dubproductions.bracket.domain.model

data class Round(
    val roundId: String = "",
    val matchIds: List<String> = listOf(),
    val roundNum: Int = 1,
    val byeParticipantId: String? = null
)
