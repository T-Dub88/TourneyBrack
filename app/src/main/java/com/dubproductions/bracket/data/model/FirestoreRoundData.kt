package com.dubproductions.bracket.data.model

data class FirestoreRoundData(
    var matchIds: List<String>? = null,
    var roundNumber: Int? = null,
    var byeParticipantId: String? = null
)
