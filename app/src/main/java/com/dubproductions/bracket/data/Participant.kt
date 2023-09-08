package com.dubproductions.bracket.data

data class Participant(
    val username: String? = null,
    val userId: String? = null,
    var points: Double? = null,
    val matches: MutableList<Match>? = null,
    var buchholz: Double? = null,
    var sonnebornBerger: Double? = null,
    var dropped: Boolean? = null,
) {
    fun updateTiebreakers() {
        buchholz = 0.0
        sonnebornBerger = 0.0

        matches?.let { matchesList ->
            for (match in matchesList) {

                val opponent = if (match.playerOne?.username != username) {
                    match.playerOne
                } else {
                    match.playerTwo
                }

                opponent?.points?.let { opponentsPoints ->

                    buchholz = buchholz!! + opponentsPoints

                    sonnebornBerger = sonnebornBerger!! + if (match.winner?.username == username) {
                        opponentsPoints
                    } else if (match.tie == true) {
                        (opponentsPoints * 0.5)
                    } else {
                        0.0
                    }

                }
            }
        }
    }

    fun dropParticipant() {
        dropped = true
    }
}
