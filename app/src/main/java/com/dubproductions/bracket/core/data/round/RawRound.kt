package com.dubproductions.bracket.core.data.round

data class RawRound(
    var roundId: String = "",
    var matchIds: List<String> = listOf(),
    var roundNum: Int = 1,
    var byeParticipantId: String? = null
)
