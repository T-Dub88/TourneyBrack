package com.dubproductions.bracket.presentation.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dubproductions.bracket.R
import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant

@Composable
fun DeclareWinnerDialog(
    selectedWinnerId: String?,
    selectedMatchId: String,
    participantList: List<Participant>,
    matchList: List<Match>,
    changeDialogVisibility: (Boolean) -> Unit,
    declareWinner: (winnerId: String?, roundNumber: Int, match: Match) -> Unit
) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.report_results)) },
        text = {
            if (selectedWinnerId == null) {
                Text(text = stringResource(id = R.string.report_tie))
            } else {
                Text(
                    text = stringResource(
                        id = R.string.report_winner,
                        participantList.find { it.userId == selectedWinnerId }?.username.toString()
                    )
                )
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
                            match.roundNum,
                            match
                        )
                    }

                    changeDialogVisibility(false)
                }
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { changeDialogVisibility(false) }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },

    )
}
