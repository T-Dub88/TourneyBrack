package com.dubproductions.bracket.domain.model

data class Participant(
    val username: String = "",
    val userId: String = "",
    val points: Double = 0.0,
    val opponentsAveragePoints: Double = 0.0,
    val opponentsOpponentsAveragePoints: Double = 0.0,
    val dropped: Boolean = false,
    val matchIds: List<String> = listOf(),
    val opponentIds: List<String> = listOf()
)
