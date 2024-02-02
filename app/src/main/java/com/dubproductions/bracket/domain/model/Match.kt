package com.dubproductions.bracket.domain.model

import com.dubproductions.bracket.utils.status.MatchStatus

data class Match(
    val matchId: String = "",
    val playerOneId: String = "",
    val playerTwoId: String? = "",
    var winnerId: String? = null,
    var tie: Boolean? = null,
    val round: Int = 0,
    var status: String = MatchStatus.PENDING.status
) {
    fun declareWinner(id: String?) {
        winnerId = id
    }

    fun declareTie() {
        tie = true
    }

    fun setMatchStatus(newStatus: String) {
        status = newStatus
    }

}
