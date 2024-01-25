package com.dubproductions.bracket.data

data class Match(
    val matchId: String = "",
    val playerOneId: String = "",
    val playerTwoId: String = "",
    var winnerId: String? = null,
    var tie: Boolean? = null,
    val round: Int = 0
) {
    fun declareWinner(id: String) {
        winnerId = id
    }

    fun declareTie() {
        tie = true
    }
}
