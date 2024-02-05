package com.dubproductions.bracket.domain.model

data class Participant(
    val username: String,
    val userId: String,
    val points: Double,
    val buchholz: Double,
    val sonnebornBerger: Double,
    val dropped: Boolean,
    val matchIds: List<String>
)
