package com.dubproductions.bracket.hosting.presentation.participants

import com.dubproductions.bracket.core.domain.model.participant.Participant

data class ParticipantsState(
    var displayCannotAddDialog: Boolean = false,
    var displayAddPlayerDialog: Boolean = false,
    var displayDropPlayerDialog: Boolean = false,
    var enabled: Boolean = true,
    var selectedParticipant: Participant = Participant(),
    var addPlayerTextFieldValue: String = ""
)
