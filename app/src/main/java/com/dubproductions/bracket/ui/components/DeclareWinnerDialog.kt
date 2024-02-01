package com.dubproductions.bracket.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.dubproductions.bracket.data.Match
import com.dubproductions.bracket.data.Participant

@Composable
fun DeclareWinnerDialog(
    selectedWinnerId: String?,
    selectedMatchId: String,
    participantList: List<Participant>,
    matchList: List<Match>,
    changeDialogVisibility: (Boolean) -> Unit,
    declareWinner: (winnerId: String?, roundNumber: Int, matchId: String) -> Unit
) {
    AlertDialog(
        title = { Text(text = "Report Results") },
        text = {
            if (selectedWinnerId == null) {
                Text(text = "Are you sure you want to declare this match a tie?")
            } else {
                Text(text = "Are you sure you want to declare ${participantList.find { it.userId == selectedWinnerId }?.username} as the winner of this match?")
            }
        },
        icon = { Icon(imageVector = Icons.Default.Check, contentDescription = null) },
        onDismissRequest = { changeDialogVisibility(false) },
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMatch = matchList.find { it.matchId == selectedMatchId }
                    selectedMatch?.let { match ->
                        declareWinner(
                            selectedWinnerId,
                            match.round,
                            match.matchId
                        )
                    }

                    changeDialogVisibility(false)
                }
            ) {
                Text(text = "Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = { changeDialogVisibility(false) }) {
                Text(text = "Cancel")
            }
        },

        )
}
