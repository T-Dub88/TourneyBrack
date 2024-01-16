package com.dubproductions.bracket.ui.main.hosting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dubproductions.bracket.ui.ReusableDialog
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun EditTournamentScreen(
    userViewModel: UserViewModel,
    hostingNavController: NavHostController
) {
    val tournament by userViewModel.viewingTournament.collectAsStateWithLifecycle()

    var displayOpenDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var displayClosedDialog by rememberSaveable {
        mutableStateOf(false)
    }

    EditTournamentScreenContent(
        tournamentName = tournament.name,
        registrationCode = tournament.id,
        numParticipants = tournament.participants?.size,
        rounds = tournament.setNumberOfRounds(),
        format = tournament.type,
        start = userViewModel.formatDateTime(tournament.timeStarted),
        status = tournament.status,
        openDialog = displayOpenDialog,
        closedDialog = displayClosedDialog,
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
            // navigate to participants screen
        },
        bracketOnClick = {
            // if bracket is empty, ask for bracket gen and then navigate
            // else navigate to bracket screen
        },
        startOnClick = {
            // if bracket empty, ask for generation
            // make end tournament if started
        },
        onCloseDialogChange = {
            displayClosedDialog = it
        },
        onOpenDialogChange = {
            displayOpenDialog = it
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
    lockOnClick: () -> Unit,
    participantsOnClick: () -> Unit,
    bracketOnClick: () -> Unit,
    startOnClick: () -> Unit,
    onCloseDialogChange: (Boolean) -> Unit,
    onOpenDialogChange: (Boolean) -> Unit
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
        participantsOnClick = {},
        bracketOnClick = {},
        lockOnClick = {},
        startOnClick = {},
        onOpenDialogChange = {},
        onCloseDialogChange = {}
    )
}
