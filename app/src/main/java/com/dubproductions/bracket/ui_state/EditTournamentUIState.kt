package com.dubproductions.bracket.ui_state

data class EditTournamentUIState(
    var displayOpenedDialog: Boolean = false,
    var displayClosedDialog: Boolean = false,
    var displayBracketGenerationDialog: Boolean = false,
    var displayDeleteTournamentDialog: Boolean = false
)
