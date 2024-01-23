package com.dubproductions.bracket.ui.main.hosting.participant

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dubproductions.bracket.data.Participant
import com.dubproductions.bracket.viewmodel.ParticipantViewModel

@Composable
fun ParticipantMatchesScreen(
    participantViewModel: ParticipantViewModel
) {
    val participant: Participant by participantViewModel.selectedParticipant.collectAsStateWithLifecycle()

    ParticipantMatchesScreenContent(participantName = participant.username)
}

@Composable
fun ParticipantMatchesScreenContent(participantName: String) {
    Text(text = participantName)
}

@Preview(
    showBackground = true
)
@Composable
fun ParticipantMatchesPreview() {
    ParticipantMatchesScreenContent(
        participantName = "T_Dub88"
    )
}
