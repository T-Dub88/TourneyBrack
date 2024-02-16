package com.dubproductions.bracket.utils.status

enum class TournamentStatus(val statusString: String) {
    REGISTERING("registering"),
    CLOSED("closed"),
    PLAYING("playing"),
    ROUNDS_COMPLETE("rounds_complete"),
    TOURNAMENT_COMPLETE("tournament_complete")
}
