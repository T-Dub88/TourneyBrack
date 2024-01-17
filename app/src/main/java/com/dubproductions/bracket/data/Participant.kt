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
    fun updateTiebreakers(participantList: List<Participant>) {
        buchholz = 0.0
        sonnebornBerger = 0.0

        matches?.let { matchesList ->
            for (match in matchesList) {

                val opponentId = if (match.playerOneId != userId) {
                    match.playerOneId
                } else {
                    match.playerTwoId
                }

                val opponent = participantList.find { opponentId == it.userId }

                opponent?.points?.let { opponentsPoints ->

                    buchholz = buchholz!! + opponentsPoints

                    sonnebornBerger = sonnebornBerger!! + if (match.winnerId == userId) {
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
