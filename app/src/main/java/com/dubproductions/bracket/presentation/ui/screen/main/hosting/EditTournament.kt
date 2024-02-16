package com.dubproductions.bracket.presentation.ui.screen.main.hosting

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dubproductions.bracket.R
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.presentation.ui.components.dialogs.ReusableDialog
import com.dubproductions.bracket.presentation.ui.state.EditTournamentUIState
import com.dubproductions.bracket.utils.TournamentHousekeeping.setNumberOfRounds
import com.dubproductions.bracket.utils.status.TournamentStatus
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EditTournamentScreen(
    tournament: Tournament,
    uiState: EditTournamentUIState,
    lockOnClick: () -> Unit,
    participantsOnClick: () -> Unit,
    bracketOnClick: () -> Unit,
    startOnClick: () -> Unit,
    deleteOnClick: () -> Unit,
    changeClosedDialogState: (Boolean) -> Unit,
    changeOpenedDialogState: (Boolean) -> Unit,
    changeBracketDialogState: (Boolean) -> Unit,
    changeDeleteDialogState: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Card(
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            EditTourneyText(
                text = stringResource(
                    id = R.string.tournament_name_2,
                    tournament.name
                )
            )

            EditTourneyText(
                text = stringResource(
                    id = R.string.registration_code,
                    tournament.tournamentId.takeLast(4)
                )
            )

            EditTourneyText(
                text = stringResource(
                    id = R.string.format,
                    tournament.type
                )
            )

            EditTourneyText(
                text = stringResource(
                    id = R.string.status,
                    tournament.status.replaceFirstChar { it.uppercase() }
                )
            )

            EditTourneyText(
                text = stringResource(
                    id = R.string.num_participants,
                    tournament.participants.size
                )
            )

            EditTourneyText(
                text = stringResource(
                    id = R.string.num_rounds,
                    tournament.setNumberOfRounds()
                )
            )

            EditTourneyText(
                text = stringResource(
                    id = R.string.start_date_time,
                    formatDateTime(tournament.timeStarted, LocalContext.current)
                )
            )
        }

        Button(
            onClick = lockOnClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(vertical = 4.dp),
            enabled = when (tournament.status) {
                TournamentStatus.REGISTERING.statusString,
                TournamentStatus.CLOSED.statusString -> true
                else -> false
            }
        ) {
            Text(
                text = when (tournament.status) {
                    TournamentStatus.REGISTERING.statusString -> {
                        stringResource(id = R.string.close_registration)
                    }
                    else -> stringResource(id = R.string.open_registration)
                }
            )
        }

        Button(
            onClick = startOnClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = when (tournament.status) {
                    TournamentStatus.REGISTERING.statusString,
                    TournamentStatus.CLOSED.statusString -> {
                        stringResource(id = R.string.start_tournament)
                    }
                    TournamentStatus.PLAYING.statusString -> {
                        stringResource(id = R.string.next_round)
                    }
                    else -> {
                        stringResource(id = R.string.complete_tournament)
                    }

                }
            )
        }

        Button(
            onClick = participantsOnClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(vertical = 4.dp)
        ) {
            Text(text = stringResource(id = R.string.view_edit_participants))
        }

        Button(
            onClick = bracketOnClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(vertical = 4.dp)
        ) {
            Text(text = stringResource(id = R.string.view_edit_bracket))
        }

        Button(
            onClick = deleteOnClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text(text = stringResource(id = R.string.delete_tournament))
        }

        when {
            uiState.displayClosedDialog -> {
                ReusableDialog(
                    titleText = stringResource(id = R.string.registration_closed),
                    contentText = stringResource(id = R.string.registration_closed_message),
                    icon = Icons.Filled.Close,
                    dismissDialog = { changeClosedDialogState(false) }
                )
            }
            uiState.displayOpenedDialog -> {
                ReusableDialog(
                    titleText = stringResource(id = R.string.registration_opened),
                    contentText = stringResource(id = R.string.registration_opened_message),
                    icon = Icons.Filled.Check,
                    dismissDialog = { changeOpenedDialogState(false) }
                )
            }
        }
        when {
            uiState.displayBracketGenerationDialog -> {
                BracketGenerationDialog(
                    title = stringResource(id = R.string.bracket_gen_title),
                    body = stringResource(id = R.string.bracket_gen_body),
                    positiveButton = stringResource(id = R.string.generate),
                    dismissButton = stringResource(id = R.string.cancel),
                    onConfirmClick = { changeBracketDialogState(true) },
                    onCancelClick = { changeBracketDialogState(false) },
                    icon = Icons.Filled.Create
                )
            }
        }
        when {
            uiState.displayDeleteTournamentDialog -> {
                BracketGenerationDialog(
                    onConfirmClick = { changeDeleteDialogState(true) },
                    onCancelClick = { changeDeleteDialogState(false) },
                    title = stringResource(id = R.string.delete_tournament),
                    body = stringResource(id = R.string.delete_message),
                    positiveButton = stringResource(id = R.string.delete),
                    dismissButton = stringResource(id = R.string.cancel),
                    icon = Icons.Filled.DeleteForever
                )
            }
        }
    }
}

@Composable
fun EditTourneyText(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        modifier = Modifier
            .padding(start = 8.dp)
            .padding(vertical = 4.dp)
    )
}

@Composable
fun BracketGenerationDialog(
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    title: String,
    body: String,
    positiveButton: String,
    dismissButton: String,
    icon: ImageVector
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = body) },
        icon = { Icon(imageVector = icon, contentDescription = null) },
        confirmButton = {
            TextButton(
                onClick = onConfirmClick
            ) {
                Text(text = positiveButton)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancelClick
            ) {
                Text(text = dismissButton)
            }
        },
        onDismissRequest = onCancelClick
    )
}

@Preview(
    showBackground = true
)
@Composable
fun EditTournamentScreenPreview() {
    EditTournamentScreen(
        tournament = Tournament(),
        uiState = EditTournamentUIState(),
        lockOnClick = {  },
        participantsOnClick = {  },
        bracketOnClick = {  },
        startOnClick = {  },
        deleteOnClick = {  },
        changeClosedDialogState = {},
        changeOpenedDialogState = {},
        changeBracketDialogState = {},
        changeDeleteDialogState = {}
    )
}

fun formatDateTime(timestamp: Long?, context: Context): String {
    return timestamp?.let {
        val sdf = SimpleDateFormat("MM-dd-yy HH:mm", Locale.getDefault())
        sdf.format(it)
    } ?: context.getString(R.string.not_started)
}