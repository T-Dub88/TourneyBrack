package com.dubproductions.bracket.data

data class Round(
    var matches: List<Match>,
    var roundNumber: Int,
    var bye: String? = null
)
