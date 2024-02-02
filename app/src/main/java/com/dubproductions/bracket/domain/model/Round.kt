package com.dubproductions.bracket.domain.model

data class Round(
    var matchIds: List<String>? = null,
    var roundNumber: Int? = null,
    var bye: String? = null
)
