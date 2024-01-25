package com.dubproductions.bracket.ui_state

import com.dubproductions.bracket.data.Participant

data class ParticipantsUIState(
    var displayCannotAddDialog: Boolean = false,
    var displayAddPlayerDialog: Boolean = false,
    var displayDropPlayerDialog: Boolean = false,
    var enabled: Boolean = true,
    var selectedParticipant: Participant = Participant(),
    var addPlayerTextFieldValue: String = ""
)
