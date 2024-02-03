package com.dubproductions.bracket.data.model

data class FirestoreUserData(
    var userId: String? = null,
    var username: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var hostingTournamentIds: List<String>? = null,
    var participatingTournamentIds: List<String>? = null
)
