package com.dubproductions.bracket.data.model

data class FirestoreRoundData(
    var roundId: String? = null,
    var matchIds: List<String>? = null,
    var roundNumber: Int? = null,
    var byeParticipantId: String? = null
)
