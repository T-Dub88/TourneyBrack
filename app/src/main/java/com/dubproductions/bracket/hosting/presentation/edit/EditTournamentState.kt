package com.dubproductions.bracket.hosting.presentation.edit

data class EditTournamentState(
    var displayOpenedDialog: Boolean = false,
    var displayClosedDialog: Boolean = false,
    var displayBracketGenerationDialog: Boolean = false,
    var displayDeleteTournamentDialog: Boolean = false,
    var displayCreateNewRoundDialog: Boolean = false,
    var displayCompleteRoundsDialog: Boolean = false,
    var displayCompleteTournamentDialog: Boolean = false,
    var displayMatchesIncompleteDialog: Boolean = false,
    var uiEnabled: Boolean = true
)
