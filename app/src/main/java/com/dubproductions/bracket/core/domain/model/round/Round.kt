package com.dubproductions.bracket.core.domain.model.round

import com.dubproductions.bracket.core.domain.model.match.Match

data class Round(
    val roundId: String = "",
    val matches: List<Match> = listOf(),
    val matchIds: List<String> = listOf(),
    val roundNum: Int = 1,
    val byeParticipantId: String? = null
)
