package com.dubproductions.bracket.ui.main.hosting.participant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dubproductions.bracket.Validation
import com.dubproductions.bracket.data.Participant
import com.dubproductions.bracket.ui_state.ParticipantsScreenUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantsScreen(
    uiState: ParticipantsScreenUIState,
    participantList: List<Participant>,
    floatingActionButtonClick: () -> Unit,
    dropPlayerOnClick: (Participant) -> Unit,
    viewMatchesOnClick: (Participant) -> Unit,
    closeCannotAddDialog: () -> Unit,
    closeAddPlayerDialog: (Boolean, String?) -> Unit,
    closeDropPlayerDialog: (Boolean) -> Unit,
    changeAddPlayerTextFieldValue: (String) -> Unit
) {
    var addPlayerTextError by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = floatingActionButtonClick
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(8.dp)
            ) {
                itemsIndexed(participantList) {index, participant ->
                    ParticipantCard(
                        standing = index + 1,
                        participant = participant,
                        dropPlayerOnClick = { dropPlayerOnClick(participant) },
                        viewMatchesOnClick = { viewMatchesOnClick(participant) }
                    )
                }
            }
        }
    }

    if (uiState.displayCannotAddDialog) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null
                )
            },
            text = { Text(text = "New players cannot be added to a tournament after the start of the first round.") },
            title = { Text(text = "Cannot Add New Player") },
            onDismissRequest = closeCannotAddDialog,
            confirmButton = {
                TextButton(onClick = closeCannotAddDialog) {
                    Text(text = "Ok")
                }
            }
        )
    }

    if (uiState.displayAddPlayerDialog) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null
                )
            },
            title = { Text(text = "Add Player") },
            text = {
                OutlinedTextField(
                    value = uiState.addPlayerTextFieldValue,
                    onValueChange = {
                        changeAddPlayerTextFieldValue(it)
                        if (addPlayerTextError) {
                            addPlayerTextError = false
                        }
                    },
                    label = { Text(text = "Enter Player Name") },
                    enabled = uiState.enabled,
                    isError = addPlayerTextError,
                    supportingText = {
                        if (addPlayerTextError) {
                            Text(text = "Must Enter Player Name")
                        }
                    }
                )
            },
            onDismissRequest = {
                if (uiState.enabled) closeAddPlayerDialog(false, null)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (Validation.isFieldEmpty(uiState.addPlayerTextFieldValue)) {
                            closeAddPlayerDialog(true, uiState.addPlayerTextFieldValue)
                        } else {
                            addPlayerTextError = true
                        }
                    },
                    enabled = uiState.enabled
                ) {
                    Text(text = "Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { closeAddPlayerDialog(false, null) },
                    enabled = uiState.enabled
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (uiState.displayDropPlayerDialog) {
        AlertDialog(
            title = {
                Text(text = "Drop ${uiState.selectedParticipant.username}")
            },
            text = {
                Text(text = "${uiState.selectedParticipant.username} will be dropped from the tournament. This action cannot be undone.")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.PersonRemove,
                    contentDescription = null
                )
            },
            onDismissRequest = { closeDropPlayerDialog(false) },
            confirmButton = {
                TextButton(
                    onClick = { closeDropPlayerDialog(true) },
                    enabled = uiState.enabled
                ) {
                    Text(text = "Drop")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { closeDropPlayerDialog(false) },
                    enabled = uiState.enabled
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}


@Composable
fun ParticipantCard(
    participant: Participant,
    standing: Int,
    dropPlayerOnClick: () -> Unit,
    viewMatchesOnClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(
           defaultElevation = 4.dp
        ),
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = participant.username,
                fontSize = 22.sp
            )
            Text(
                text = if (participant.dropped) {
                    "Dropped"
                } else {
                    "Place: $standing"
                },
                fontSize = 22.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Points: ${participant.points}",
                fontSize = 18.sp
            )
            Text(
                text = "Buchholz: ${participant.buchholz}",
                fontSize = 18.sp
            )
            Text(
                text = "SB: ${participant.sonnebornBerger}",
                fontSize = 18.sp
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(
                onClick = dropPlayerOnClick
            ) {
                Text(text = "Drop Player", fontSize = 16.sp)
            }
            TextButton(
                onClick = viewMatchesOnClick
            ) {
                Text(text = "View Matches", fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ParticipantsScreenPreview() {
    ParticipantsScreen(
        uiState = ParticipantsScreenUIState(),
        participantList = listOf(
            Participant(
                username = "T_Dub88",
                points = 1.2,
                buchholz = 1.2,
                sonnebornBerger = 1.3,
                dropped = false,
                userId = ""
            )
        ),
        floatingActionButtonClick = {},
        dropPlayerOnClick = {},
        viewMatchesOnClick = {},
        closeCannotAddDialog = {},
        closeAddPlayerDialog = { _, _ -> },
        closeDropPlayerDialog = {},
        changeAddPlayerTextFieldValue = {}
    )
}
