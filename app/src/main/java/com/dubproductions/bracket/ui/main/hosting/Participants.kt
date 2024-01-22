package com.dubproductions.bracket.ui.main.hosting

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dubproductions.bracket.data.Participant
import com.dubproductions.bracket.data.TournamentStatus
import com.dubproductions.bracket.viewmodel.ParticipantViewModel
import com.dubproductions.bracket.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun ParticipantsScreen(
    userViewModel: UserViewModel,
    participantViewModel: ParticipantViewModel = hiltViewModel()
) {
    val tournament by userViewModel.viewingTournament.collectAsStateWithLifecycle()
    val selectedParticipant by participantViewModel.selectedParticipant.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    var displayCannotAddDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var displayAddPlayerDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var displayDropPlayerDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var addPlayerTextFieldValue by rememberSaveable {
        mutableStateOf("")
    }
    var fieldsEnabled by rememberSaveable {
        mutableStateOf(true)
    }

    ParticipantsScreenContent(
        floatingActionButtonClick = {
            when(tournament.status) {
                TournamentStatus.REGISTERING.status,
                TournamentStatus.CLOSED.status -> {
                    displayAddPlayerDialog = true
                }
                else -> {
                    displayCannotAddDialog = true
                }
            }
        },
        dropPlayerOnClick = { participant ->
            participantViewModel.updateSelectedParticipant(participant)
            displayDropPlayerDialog = true
        },
        viewMatchesOnClick = { /*TODO*/ },
        participantList = tournament.sortPlayerStandings(),
        displayAddPlayerDialog = displayAddPlayerDialog,
        displayCannotAddDialog = displayCannotAddDialog,
        displayDropPlayerDialog = displayDropPlayerDialog,
        addPlayerTextFieldString = addPlayerTextFieldValue,
        fieldsEnabled = fieldsEnabled,
        cannotAddPlayerCloseDialog = { displayCannotAddDialog = false },
        addPlayerTextFieldEdit = { addPlayerTextFieldValue = it },
        addPlayerCloseDialog = {
            if (it) {
                fieldsEnabled = false
                coroutineScope.launch {
                    participantViewModel.addNewPlayerToTournament(
                        participantUserName = addPlayerTextFieldValue,
                        tournamentId = tournament.id!!
                    )
                    displayAddPlayerDialog = false
                    addPlayerTextFieldValue = ""
                    fieldsEnabled = true
                }
            } else {
                displayAddPlayerDialog = false
                addPlayerTextFieldValue = ""
            }
        },
        dropPlayerCloseDialog = {
            if (it) {
                fieldsEnabled = false
                coroutineScope.launch {
                    participantViewModel.dropExistingPlayer(
                        tournamentId = tournament.id!!,
                        participant = selectedParticipant,
                        tournamentStatus = tournament.status
                    )
                    displayDropPlayerDialog = false
                    fieldsEnabled = true
                }
            } else {
                displayDropPlayerDialog = false
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantsScreenContent(
    participantList: List<Participant>?,
    displayCannotAddDialog: Boolean,
    displayAddPlayerDialog: Boolean,
    displayDropPlayerDialog: Boolean,
    addPlayerTextFieldString: String,
    fieldsEnabled: Boolean,
    floatingActionButtonClick: () -> Unit,
    dropPlayerOnClick: (Participant) -> Unit,
    viewMatchesOnClick: () -> Unit,
    addPlayerCloseDialog: (Boolean) -> Unit,
    cannotAddPlayerCloseDialog: () -> Unit,
    addPlayerTextFieldEdit: (String) -> Unit,
    dropPlayerCloseDialog: (Boolean) -> Unit
) {
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
                participantList?.let {
                    itemsIndexed(it) {index, participant ->
                        ParticipantCard(
                            standing = index + 1,
                            participant = participant,
                            dropPlayerOnClick = dropPlayerOnClick,
                            viewMatchesOnClick = viewMatchesOnClick
                        )
                    }
                }
            }
        }
    }

    if (displayCannotAddDialog) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null
                )
            },
            text = { Text(text = "New players cannot be added to a tournament after the start of the first round.") },
            title = { Text(text = "Cannot Add New Player") },
            onDismissRequest = cannotAddPlayerCloseDialog,
            confirmButton = {
                TextButton(onClick = cannotAddPlayerCloseDialog) {
                    Text(text = "Ok")
                }
            }
        )
    }

    if (displayAddPlayerDialog) {
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
                    value = addPlayerTextFieldString,
                    onValueChange = { addPlayerTextFieldEdit(it) },
                    label = { Text(text = "Enter Player Name") },
                    enabled = fieldsEnabled
                )
            },
            onDismissRequest = {
                if (fieldsEnabled) addPlayerCloseDialog(false)
            },
            confirmButton = {
                TextButton(
                    onClick = { addPlayerCloseDialog(true) },
                    enabled = fieldsEnabled
                ) {
                    Text(text = "Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { addPlayerCloseDialog(false) },
                    enabled = fieldsEnabled
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (displayDropPlayerDialog) {
        AlertDialog(
            title = {
                Text(text = "Drop Player")
            },
            text = {
                Text(text = "The player will be dropped from the tournament. This action cannot be undone.")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.PersonRemove,
                    contentDescription = null
                )
            },
            onDismissRequest = { dropPlayerCloseDialog(false) },
            confirmButton = {
                TextButton(
                    onClick = { dropPlayerCloseDialog(true) },
                    enabled = fieldsEnabled
                ) {
                    Text(text = "Drop")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { dropPlayerCloseDialog(false) },
                    enabled = fieldsEnabled
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
    dropPlayerOnClick: (Participant) -> Unit,
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
                onClick = {
                    dropPlayerOnClick(participant)
                }
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
    ParticipantsScreenContent(
        floatingActionButtonClick = {},
        dropPlayerOnClick = {},
        viewMatchesOnClick = {},
        addPlayerCloseDialog = {},
        cannotAddPlayerCloseDialog = {},
        addPlayerTextFieldEdit = {},
        dropPlayerCloseDialog = {},
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
        displayAddPlayerDialog = false,
        displayCannotAddDialog = false,
        addPlayerTextFieldString = "",
        fieldsEnabled = true,
        displayDropPlayerDialog = false
    )
}
