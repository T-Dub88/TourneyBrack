package com.dubproductions.bracket.data

data class Round(
    var matches: List<Match> = listOf(),
    var roundNumber: Int = 1,
    var bye: String? = null
)
