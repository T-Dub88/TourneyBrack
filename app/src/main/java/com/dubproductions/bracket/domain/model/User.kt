package com.dubproductions.bracket.domain.model

data class User(
    val userId: String = "",
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val hostingTournamentIds: List<String> = listOf(),
    val participatingTournamentIds: List<String> = listOf(),
    val completedTournamentIds: List<String> = listOf()
)
