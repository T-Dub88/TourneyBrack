package com.dubproductions.bracket.data

data class Match(
    val playerOne: Participant? = null,
    val playerTwo: Participant? = null,
    var winner: Participant? = null,
    var tie: Boolean? = null,
    val round: Int? = null
) {
    fun setWinner(participant: Participant) {
        winner = participant
    }

    fun setTie() {
        tie = true
    }
}
