package com.dubproductions.bracket.hosting.presentation.edit

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
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dubproductions.bracket.R
import com.dubproductions.bracket.core.domain.model.tournament.Tournament
import com.dubproductions.bracket.hosting.presentation.edit.components.BracketGenerationDialog
import com.dubproductions.bracket.core.presentation.components.ReusableDialog
import com.dubproductions.bracket.core.utils.TournamentHousekeeping.setNumberOfRounds
import com.dubproductions.bracket.core.domain.model.tournament.TournamentStatus
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EditTournamentScreen(
    tournament: Tournament,
    uiState: EditTournamentState,
    lockOnClick: () -> Unit,
    participantsOnClick: () -> Unit,
    bracketOnClick: () -> Unit,
    startOnClick: () -> Unit,
    deleteOnClick: () -> Unit,
    changeClosedDialogState: (Boolean) -> Unit,
    changeOpenedDialogState: (Boolean) -> Unit,
    changeBracketDialogState: (Boolean) -> Unit,
    changeDeleteDialogState: (Boolean) -> Unit,
    changeNextRoundDialogState: (Boolean) -> Unit,
    changeCompleteRoundsDialogState: (Boolean) -> Unit,
    changeCompleteTournamentDialogState: (Boolean) -> Unit,
    changeIncompleteMatchDialogState: (Boolean) -> Unit
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
                    when (tournament.status) {
                        TournamentStatus.PLAYING.statusString,
                        TournamentStatus.COMPLETE_ROUNDS.statusString-> {
                            stringResource(id = R.string.playing_round, tournament.rounds.size)
                        }
                        TournamentStatus.COMPLETE_TOURNAMENT.statusString -> {
                            stringResource(id = R.string.awaiting_confirmation)
                        }
                        TournamentStatus.FINALIZED.statusString -> {
                            stringResource(id = R.string.complete)
                        }
                        else -> { tournament.status.replaceFirstChar { it.uppercase() } }
                    }

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
            enabled = if (uiState.uiEnabled) {
                when (tournament.status) {
                    TournamentStatus.REGISTERING.statusString,
                    TournamentStatus.CLOSED.statusString -> true
                    else -> false
                }
            } else {
                false
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
                .padding(vertical = 4.dp),
            enabled = uiState.uiEnabled
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
                    TournamentStatus.COMPLETE_ROUNDS.statusString -> {
                        stringResource(id = R.string.finalize_rounds)
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
                .padding(vertical = 4.dp),
            enabled = uiState.uiEnabled
        ) {
            Text(text = stringResource(id = R.string.view_edit_participants))
        }

        Button(
            onClick = bracketOnClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(vertical = 4.dp),
            enabled = uiState.uiEnabled
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
            ),
            enabled = uiState.uiEnabled
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

            uiState.displayMatchesIncompleteDialog -> {
                ReusableDialog(
                    titleText = "Incomplete Matches",
                    contentText = "Complete all matches this round to advance.",
                    icon = Icons.Default.Warning,
                    dismissDialog = { changeIncompleteMatchDialogState(false) }
                )
            }

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

            uiState.displayCreateNewRoundDialog -> {
                BracketGenerationDialog(
                    onConfirmClick = { changeNextRoundDialogState(true) },
                    onCancelClick = { changeNextRoundDialogState(false) },
                    title = stringResource(
                        id = R.string.generate_round,
                        tournament.rounds.size + 1
                    ),
                    body = stringResource(
                        id = R.string.generate_round_body,
                        tournament.rounds.size,
                        tournament.rounds.size + 1
                    ),
                    positiveButton = stringResource(id = R.string.ok),
                    dismissButton = stringResource(id = R.string.cancel),
                    icon = Icons.Default.SkipNext
                )
            }

            uiState.displayCompleteRoundsDialog -> {
                BracketGenerationDialog(
                    onConfirmClick = { changeCompleteRoundsDialogState(true) },
                    onCancelClick = { changeCompleteRoundsDialogState(false) },
                    title = stringResource(id = R.string.complete_rounds),
                    body = stringResource(id = R.string.complete_rounds_body),
                    positiveButton = stringResource(id = R.string.ok),
                    dismissButton = stringResource(id = R.string.cancel),
                    icon = Icons.Default.Check
                )
            }

            uiState.displayCompleteTournamentDialog -> {
                BracketGenerationDialog(
                    onConfirmClick = { changeCompleteTournamentDialogState(true) },
                    onCancelClick = { changeCompleteTournamentDialogState(false) },
                    title = stringResource(id = R.string.complete_tournament),
                    body = stringResource(id = R.string.complete_tournament_body),
                    positiveButton = stringResource(id = R.string.ok),
                    dismissButton = stringResource(id = R.string.wait),
                    icon = Icons.Default.Warning
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

@Preview(
    showBackground = true
)
@Composable
fun EditTournamentScreenPreview() {
    EditTournamentScreen(
        tournament = Tournament(),
        uiState = EditTournamentState(
            uiEnabled = false
        ),
        lockOnClick = {  },
        participantsOnClick = {  },
        bracketOnClick = {  },
        startOnClick = {  },
        deleteOnClick = {  },
        changeClosedDialogState = {},
        changeOpenedDialogState = {},
        changeBracketDialogState = {},
        changeDeleteDialogState = {},
        changeNextRoundDialogState = {},
        changeCompleteRoundsDialogState = {},
        changeCompleteTournamentDialogState = {},
        changeIncompleteMatchDialogState = {}
    )
}

fun formatDateTime(timestamp: Long?, context: Context): String {
    return timestamp?.let {
        val sdf = SimpleDateFormat("MM-dd-yy HH:mm", Locale.getDefault())
        sdf.format(it)
    } ?: context.getString(R.string.not_started)
}