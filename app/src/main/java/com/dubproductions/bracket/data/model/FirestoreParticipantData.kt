package com.dubproductions.bracket.data.model

data class FirestoreParticipantData(
    var username: String? = null,
    var userId: String? = null,
    var points: Double? = null,
    var matchIds: List<String>? = null,
    var buchholz: Double? = null,
    var sonnebornBerger: Double? = null,
    var dropped: Boolean? = null,
)
