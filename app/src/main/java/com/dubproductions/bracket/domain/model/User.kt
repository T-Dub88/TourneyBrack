package com.dubproductions.bracket.domain.model

data class User(
    val userId: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val hostingTournaments: List<Tournament>,
    val participatingTournaments: List<Tournament>
)
