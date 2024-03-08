package com.dubproductions.bracket.hosting.presentation.participants

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dubproductions.bracket.R
import com.dubproductions.bracket.core.domain.model.participant.Participant
import com.dubproductions.bracket.core.utils.Validation

@Composable
fun ParticipantsScreen(
    uiState: ParticipantsState,
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
            if (participantList.isNotEmpty() && participantList[0].matchIds.isNotEmpty()) return@Scaffold
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
                itemsIndexed(participantList) { index, participant ->
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
            text = { Text(text = stringResource(id = R.string.cannot_add_body)) },
            title = { Text(text = stringResource(id = R.string.cannot_add_title)) },
            onDismissRequest = closeCannotAddDialog,
            confirmButton = {
                TextButton(onClick = closeCannotAddDialog) {
                    Text(text = stringResource(id = R.string.ok))
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
            title = { Text(text = stringResource(id = R.string.add_player_title)) },
            text = {
                OutlinedTextField(
                    value = uiState.addPlayerTextFieldValue,
                    onValueChange = {
                        changeAddPlayerTextFieldValue(it)
                        if (addPlayerTextError) {
                            addPlayerTextError = false
                        }
                    },
                    label = { Text(text = stringResource(id = R.string.enter_player_name)) },
                    enabled = uiState.enabled,
                    isError = addPlayerTextError,
                    supportingText = {
                        if (addPlayerTextError) {
                            Text(text = stringResource(id = R.string.add_player_error))
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
                    Text(text = stringResource(id = R.string.add))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { closeAddPlayerDialog(false, null) },
                    enabled = uiState.enabled
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }

    if (uiState.displayDropPlayerDialog) {
        AlertDialog(
            title = {
                Text(
                    text = stringResource(
                        id = R.string.drop_title,
                        uiState.selectedParticipant.username
                    )
                )
            },
            text = {
                Text(
                    text = stringResource(
                        id = R.string.drop_body,
                        uiState.selectedParticipant.username
                    )
                )
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
                    Text(text = stringResource(id = R.string.drop))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { closeDropPlayerDialog(false) },
                    enabled = uiState.enabled
                ) {
                    Text(text = stringResource(id = R.string.cancel))
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
            containerColor = MaterialTheme.colorScheme.surface,
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
                    stringResource(id = R.string.dropped)
                } else {
                    stringResource(id = R.string.place, standing)
                },
                fontSize = 22.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = stringResource(id = R.string.points, participant.points),
                fontSize = 18.sp
            )
            Text(
                text = stringResource(id = R.string.opponents_average_points, participant.opponentsAveragePoints),
                fontSize = 18.sp
            )
            Text(
                text = stringResource(id = R.string.opponents_opponents_average_points, participant.opponentsOpponentsAveragePoints),
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
                Text(text = stringResource(id = R.string.drop_player), fontSize = 16.sp)
            }
            TextButton(
                onClick = viewMatchesOnClick
            ) {
                Text(text = stringResource(id = R.string.view_matches), fontSize = 16.sp)
            }
        }
    }
}

@Preview
@Composable
fun ParticipantsScreenPreview() {
    ParticipantsScreen(
        uiState = ParticipantsState(),
        participantList = listOf(
            Participant(
                username = "T_Dub88",
                points = 1.2,
                opponentsAveragePoints = 1.2,
                opponentsOpponentsAveragePoints = 1.3,
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
