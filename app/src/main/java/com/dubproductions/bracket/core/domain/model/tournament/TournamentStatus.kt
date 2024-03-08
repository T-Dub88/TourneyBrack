package com.dubproductions.bracket.core.domain.model.tournament

enum class TournamentStatus(val statusString: String) {
    REGISTERING("registering"),
    CLOSED("closed"),
    PLAYING("playing"),
    COMPLETE_ROUNDS("complete_rounds"),
    COMPLETE_TOURNAMENT("tournament_complete"),
    FINALIZED("finalized")
}
