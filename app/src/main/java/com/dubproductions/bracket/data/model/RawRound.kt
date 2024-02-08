package com.dubproductions.bracket.data.model

data class RawRound(
    var roundId: String = "",
    var matchIds: List<String> = listOf(),
    var roundNum: Int = 1,
    var byeParticipantId: String? = null
)
