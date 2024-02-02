package com.dubproductions.bracket.presentation.ui.state

data class EditTournamentUIState(
    var displayOpenedDialog: Boolean = false,
    var displayClosedDialog: Boolean = false,
    var displayBracketGenerationDialog: Boolean = false,
    var displayDeleteTournamentDialog: Boolean = false
)