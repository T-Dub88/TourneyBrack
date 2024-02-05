package com.dubproductions.bracket.domain.model

data class User(
    val userId: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val hostingTournamentIds: List<String>,
    val participatingTournamentIds: List<String>,
    val completedTournamentIds: List<String>
)
