package com.dubproductions.bracket.data

data class Match(
    val playerOneId: String,
    val playerTwoId: String,
    var winnerId: String? = null,
    var tie: Boolean? = null,
    val round: Int
) {
    fun declareWinner(id: String) {
        winnerId = id
    }

    fun declareTie() {
        tie = true
    }
}
