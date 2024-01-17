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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dubproductions.bracket.navigation.Screen
import com.dubproductions.bracket.ui.ReusableDialog
import com.dubproductions.bracket.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun EditTournamentScreen(
    userViewModel: UserViewModel,
    hostingNavController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()

    val tournament by userViewModel.viewingTournament.collectAsStateWithLifecycle()

    var displayOpenDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var displayClosedDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var displayBracketGenerationDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var displayDeleteTournamentDialog by rememberSaveable {
        mutableStateOf(false)
    }

    EditTournamentScreenContent(
        tournamentName = tournament.name,
        registrationCode = tournament.id?.takeLast(4),
        numParticipants = tournament.participants?.size,
        rounds = tournament.setNumberOfRounds(),
        format = tournament.type,
        start = userViewModel.formatDateTime(tournament.timeStarted),
        status = tournament.status,
        openDialog = displayOpenDialog,
        closedDialog = displayClosedDialog,
        bracketDialog = displayBracketGenerationDialog,
        deleteDialog = displayDeleteTournamentDialog,
        lockOnClick = {

            tournament.status?.let { status ->
                when (status) {
                    "registering" -> {
                        displayClosedDialog = true
                        userViewModel.updateTournamentStatus(
                            id = tournament.id!!,
                            status = "closed"
                        )
                    }
                    "closed" -> {
                        displayOpenDialog = true
                        userViewModel.updateTournamentStatus(
                            id = tournament.id!!,
                            status = "registering"
                        )
                    }
                }
            }


        },
        participantsOnClick = {
            hostingNavController.navigate(Screen.Participants.route)
        },
        bracketOnClick = {
            if (tournament.rounds.isNullOrEmpty()) {
                displayBracketGenerationDialog = true
            } else {
                hostingNavController.navigate(Screen.Bracket.route)
            }
        },
        startOnClick = {
            // TODO: if bracket empty, ask for generation
            // TODO: make end tournament if started
        },
        onCloseDialogChange = {
            displayClosedDialog = it
        },
        onOpenDialogChange = {
            displayOpenDialog = it
        },
        closeBracketDialog = {
            if (it) {
                coroutineScope.launch {
                    userViewModel.generateBracket(tournament)
                }
            }
            displayBracketGenerationDialog = false
        },
        deleteOnClick = {
            displayDeleteTournamentDialog = true
        },
        onDeleteDialogClose = {
            if (it) {
                coroutineScope.launch {
                    userViewModel.deleteTournament(tournament.id!!)
                    hostingNavController.popBackStack()
                }
            } else {
                displayDeleteTournamentDialog = false
            }
        }
    )

}

@Composable
fun EditTournamentScreenContent(
    tournamentName: String?,
    registrationCode: String?,
    numParticipants: Int?,
    rounds: Int?,
    format: String?,
    start: String?,
    status: String?,
    closedDialog: Boolean,
    openDialog: Boolean,
    bracketDialog: Boolean,
    deleteDialog: Boolean,
    lockOnClick: () -> Unit,
    participantsOnClick: () -> Unit,
    bracketOnClick: () -> Unit,
    startOnClick: () -> Unit,
    onCloseDialogChange: (Boolean) -> Unit,
    onOpenDialogChange: (Boolean) -> Unit,
    closeBracketDialog: (Boolean) -> Unit,
    deleteOnClick: () -> Unit,
    onDeleteDialogClose: (Boolean) -> Unit
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

        EditTourneyText(text = "Tournament Name: $tournamentName")

        EditTourneyText(text = "Registration Code: $registrationCode")

        EditTourneyText(text = "Format: $format")

        EditTourneyText(text = "Status: ${status?.replaceFirstChar { it.uppercase() }}")

        EditTourneyText(text = "Number of Participants: $numParticipants")

        EditTourneyText(text = "Number of Rounds: $rounds")

        EditTourneyText(text = "Start Date/Time: $start")

        Button(
            onClick = lockOnClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(vertical = 4.dp),
            enabled = when (status) {
                "registering", "closed" -> true
                else -> false
            }
        ) {
            EditTourneyText(
                text = when (status) {
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
            closedDialog -> {
                ReusableDialog(
                    titleText = "Registration Closed!",
                    contentText = "Players will no longer be able to sign up for your tournament.",
                    icon = Icons.Filled.Close
                ) {
                   onCloseDialogChange(false)
                }
            }
            openDialog -> {
                ReusableDialog(
                    titleText = "Registration Opened!",
                    contentText = "Players are now able to register for your tournament using the registration code.",
                    icon = Icons.Filled.Check
                ) {
                    onOpenDialogChange(false)
                }
            }
        }
        when {
            bracketDialog -> {
                BracketGenerationDialog(
                    title = "Bracket Not Generated",
                    body = "Would you like to generate a bracket?",
                    positiveButton = "Generate",
                    dismissButton = "Cancel",
                    onConfirmClick = { closeBracketDialog(true) },
                    onCancelClick = { closeBracketDialog(false) },
                    icon = Icons.Filled.Create
                )
            }
        }
        when {
            deleteDialog -> {
                BracketGenerationDialog(
                    onConfirmClick = { onDeleteDialogClose(true) },
                    onCancelClick = { onDeleteDialogClose(false) },
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
    EditTournamentScreenContent(
        tournamentName = "Classic",
        registrationCode = "123ab",
        numParticipants = 8,
        rounds = 3,
        format = "Swiss",
        start = "January 4th 8:46 PM",
        status = "registering",
        openDialog = false,
        closedDialog = false,
        bracketDialog = false,
        deleteDialog = false,
        participantsOnClick = {},
        bracketOnClick = {},
        lockOnClick = {},
        startOnClick = {},
        onOpenDialogChange = {},
        onCloseDialogChange = {},
        closeBracketDialog = {},
        deleteOnClick = {},
        onDeleteDialogClose = {}
    )
}
