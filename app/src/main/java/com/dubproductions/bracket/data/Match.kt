package com.dubproductions.bracket.data

data class Match(
    val playerOneId: String? = null,
    val playerTwoId: String? = null,
    var winnerId: String? = null,
    var tie: Boolean? = null,
    val round: Int? = null
) {
    fun declareWinner(id: String) {
        winnerId = id
    }

    fun declareTie() {
        tie = true
    }
}
