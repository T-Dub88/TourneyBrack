package com.dubproductions.bracket.data.model

data class FirestoreMatchData(
    var matchId: String? = null,
    var playerOneId: String? = null,
    var playerTwoId: String? = null,
    var winnerId: String? = null,
    var tie: Boolean? = null,
    var roundNum: Int? = null,
    var status: String? = null
)
