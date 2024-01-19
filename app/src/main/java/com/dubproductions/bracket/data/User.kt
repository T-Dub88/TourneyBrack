package com.dubproductions.bracket.data

data class User(
    var userId: String? = null,
    var username: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var hostTournaments: List<String>? = null,
    var participatingTournaments: List<String>? = null
)
