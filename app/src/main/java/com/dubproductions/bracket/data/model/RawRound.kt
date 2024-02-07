package com.dubproductions.bracket.data.model

data class RawRound(
    val roundId: String = "",
    val matchIds: List<String> = listOf(),
    val roundNum: Int = 1,
    val byeParticipantId: String? = null
)
