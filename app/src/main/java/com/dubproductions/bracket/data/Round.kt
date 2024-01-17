package com.dubproductions.bracket.data

data class Round(
    var matches: List<Match>? = null,
    var roundNumber: Int? = null,
    var bye: String? = null
)
