package com.dubproductions.bracket.domain.model

data class Participant(
    val username: String? = null,
    val userId: String? = null,
    var points: Double? = null,
    val matchIds: List<String>? = null,
    var buchholz: Double? = null,
    var sonnebornBerger: Double? = null,
    var dropped: Boolean? = null,
)
