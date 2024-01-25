package com.dubproductions.bracket.ui.main.hosting

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dubproductions.bracket.data.Tournament
import com.dubproductions.bracket.ui.ReusableDialog
import com.dubproductions.bracket.ui_state.EditTournamentUIState
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
        Text(
            text = "Edit Tournament",
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            fontSize = 25.sp
        )

        EditTourneyText(text = "Tournament Name: ${tournament.name}")

        EditTourneyText(text = "Registration Code: ${tournament.id?.takeLast(4)}")

        EditTourneyText(text = "Format: ${tournament.type}")

        EditTourneyText(text = "Status: ${tournament.status.replaceFirstChar { it.uppercase() }}")

        EditTourneyText(text = "Number of Participants: ${tournament.participants.size}")

        EditTourneyText(text = "Number of Rounds: ${tournament.rounds?.size}")

        EditTourneyText(text = "Start Date/Time: ${formatDateTime(tournament.timeStarted)}")

        Button(
            onClick = lockOnClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(vertical = 4.dp),
            enabled = when (tournament.status) {
                "registering", "closed" -> true
                else -> false
            }
        ) {
            EditTourneyText(
                text = when (tournament.status) {
                    "registering" -> "Close Registration"
                    else -> "Open Registration"
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
            EditTourneyText(text = "View/Edit Participants")
        }

        Button(
            onClick = bracketOnClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(vertical = 4.dp)
        ) {
            EditTourneyText(text = "View/Edit Bracket")
        }

        Button(
            onClick = startOnClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(vertical = 4.dp)
        ) {
            EditTourneyText(text = "Start Tournament")
        }

        Button(
            onClick = deleteOnClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            EditTourneyText(text = "Delete Tournament")
        }

        when {
            uiState.displayClosedDialog -> {
                ReusableDialog(
                    titleText = "Registration Closed!",
                    contentText = "Players will no longer be able to sign up for your tournament.",
                    icon = Icons.Filled.Close,
                    dismissDialog = { changeClosedDialogState(false) }
                )
            }
            uiState.displayOpenedDialog -> {
                ReusableDialog(
                    titleText = "Registration Opened!",
                    contentText = "Players are now able to register for your tournament using the registration code.",
                    icon = Icons.Filled.Check,
                    dismissDialog = { changeOpenedDialogState(false) }
                )
            }
        }
        when {
            uiState.displayBracketGenerationDialog -> {
                BracketGenerationDialog(
                    title = "Bracket Not Generated",
                    body = "Would you like to generate a bracket?",
                    positiveButton = "Generate",
                    dismissButton = "Cancel",
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
                    title = "Delete Tournament",
                    body = "Are you sure? This action cannot be undone",
                    positiveButton = "Delete",
                    dismissButton = "Cancel",
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

fun formatDateTime(timestamp: Long?): String {
    return timestamp?.let {
        val sdf = SimpleDateFormat("MM-dd-yy HH:mm", Locale.getDefault())
        sdf.format(it)
    } ?: "Not Started"
}