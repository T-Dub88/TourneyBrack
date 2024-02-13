package com.dubproductions.bracket.presentation.ui.state

import com.dubproductions.bracket.domain.model.Participant

data class ParticipantsUIState(
    var displayCannotAddDialog: Boolean = false,
    var displayAddPlayerDialog: Boolean = false,
    var displayDropPlayerDialog: Boolean = false,
    var enabled: Boolean = true,
    var selectedParticipant: Participant = Participant(),
    var addPlayerTextFieldValue: String = ""
)
