package com.dubproductions.bracket.domain.model

import com.dubproductions.bracket.utils.status.MatchStatus

data class Match(
    var matchId: String? = null,
    var playerOneId: String? = null,
    var playerTwoId: String? = null,
    var winnerId: String? = null,
    var tie: Boolean? = null,
    var round: Int? = null,
    var status: String? = null
)
