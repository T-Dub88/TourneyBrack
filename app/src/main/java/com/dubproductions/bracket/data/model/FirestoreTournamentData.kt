package com.dubproductions.bracket.data.model

data class FirestoreTournamentData(
    var name: String? = null,
    var tournamentId: String? = null,
    var type: String? = null,
    var roundIds: List<String>? = null,
    var participantIds: List<String>? = null,
    var status: String? = null,
    var timeStarted: Long? = null,
    var timeCompleted: Long? = null,
    var hostId: String? = null
)
