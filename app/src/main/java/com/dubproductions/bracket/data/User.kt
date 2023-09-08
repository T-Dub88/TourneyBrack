package com.dubproductions.bracket.data

data class User(
    var userId: String? = null,
    var username: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var tournaments: List<String>? = null // TournamentIDs
)
