package com.dubproductions.bracket.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dubproductions.bracket.R
import com.dubproductions.bracket.data.Match
import com.dubproductions.bracket.data.Participant
import com.dubproductions.bracket.data.Tournament
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingTextField(
    text: String,
    whenChanged: (String) -> Unit,
    hint: String,
    leadingIcon: ImageVector,
    trailingIcon: ImageVector,
    trailingIconOnClick: () -> Unit,
    visualTransformation: VisualTransformation,
    error: Boolean,
    errorText: String,
    enabled: Boolean
) {
    OutlinedTextField(
        value = text,
        onValueChange = whenChanged,
        label = { Text(text = hint) },
        leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
        trailingIcon = {
            if (error) {
                Icon(imageVector = Icons.Filled.Error, contentDescription = null)
            } else {
                IconButton(onClick = trailingIconOnClick) {
                    Icon(imageVector = trailingIcon, contentDescription = null)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 25.dp
            ),
        visualTransformation = visualTransformation,
        isError = error,
        supportingText = {
            if (error) {
                Text(text = errorText)
            }
        },
        enabled = enabled
    )
}

@Composable
fun OnboardingButton(
    whenClicked: () -> Unit,
    buttonText: String,
    enabled: Boolean
) {
    Button(
        onClick = whenClicked,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp),
        enabled = enabled
    ) {
        Text(text = buttonText)
    }
}

@Composable
fun ReusableDialog(
    titleText: String,
    contentText: String,
    icon: ImageVector,
    dismissDialog: () -> Unit
) {
    AlertDialog(
        title = { Text(text = titleText) },
        text = { Text(text = contentText) },
        icon = { Icon(imageVector = icon, contentDescription = null) },
        onDismissRequest = { dismissDialog() },
        confirmButton = {
            TextButton(
                onClick = { dismissDialog() }
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentSummaryCard(
    tournament: Tournament,
    onPress: () -> Unit,
    tapCardInstruction: String
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
        ),
        onClick = onPress
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(
                    text = stringResource(
                        id = R.string.tournament_name,
                        tournament.name
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.rounds,
                        tournament.setNumberOfRounds()
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.status,
                        tournament.status.replaceFirstChar { it.uppercase() }
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        id = R.string.type,
                        tournament.type
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.started_date,
                        if (tournament.timeStarted != null) {
                            formatDateTime(tournament.timeStarted)
                        } else {
                            stringResource(id = R.string.pending)
                        }

                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.completed_date,
                        if (tournament.timeCompleted != null) {
                            formatDateTime(tournament.timeCompleted)
                        } else {
                            stringResource(R.string.pending)
                        }
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
            }
        }
        Text(
            text = tapCardInstruction,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MatchCard(
    match: Match,
    winnerClickEnabled: Boolean,
    getPlayerInfo: (String) -> Participant,
    setWinnerClick: (String?) -> Unit
) {
    val playerOne = getPlayerInfo(match.playerOneId)
    val playerTwo = match.playerTwoId?.let { getPlayerInfo(it) }

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = "Round: ${match.round}",
            modifier = Modifier
                .align(
                    alignment = Alignment.CenterHorizontally
                ),
            fontSize = 25.sp
        )
        Text(
            text = "Match Status: ${match.status.replaceFirstChar { it.uppercase() }}",
            modifier = Modifier
                .align(
                    alignment = Alignment.CenterHorizontally
                )
        )
        Row(
            modifier = Modifier
                .height(intrinsicSize = IntrinsicSize.Max)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PlayerColumn(
                participant = playerOne,
                matchResult = setPlayerMatchResult(
                    tie = match.tie,
                    winnerId = match.winnerId,
                    playerId = playerOne.userId
                ),
                setWinnerClick = setWinnerClick,
                winnerClickEnabled = winnerClickEnabled
            )
            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight(),
                color = MaterialTheme.colorScheme.onSurface
            )

            PlayerColumn(
                participant = playerTwo,
                matchResult = setPlayerMatchResult(
                    tie = match.tie,
                    winnerId = match.winnerId,
                    playerId = playerTwo?.userId ?: ""
                ),
                setWinnerClick = setWinnerClick,
                winnerClickEnabled = winnerClickEnabled
            )
        }
        Button(
            onClick = { setWinnerClick(null) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 4.dp),
            enabled = winnerClickEnabled
        ) {
            Text(text = "Tie")
        }
    }
}

@Composable
fun PlayerColumn(
    participant: Participant?,
    matchResult: String,
    setWinnerClick: (String) -> Unit,
    winnerClickEnabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = participant?.username ?: "Bye",
            fontSize = 25.sp
        )
        Text(text = "Match Result: $matchResult")
        Button(
            onClick = { setWinnerClick(participant?.userId ?: "") },
            enabled = winnerClickEnabled
        ) {
            Text(text = "Winner")
        }
    }
}

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

fun formatDateTime(timestamp: Long?): String {
    val sdf = SimpleDateFormat("MM-dd-yy HH:mm", Locale.getDefault())
    return sdf.format(timestamp)
}

fun setPlayerMatchResult(
    tie: Boolean?,
    winnerId: String?,
    playerId: String
): String {
    return if (tie == true) {
        "Tie"
    } else if (winnerId == playerId) {
        "Win"
    } else if (!winnerId.isNullOrEmpty()) {
        "Lose"
    } else {
        "Pending"
    }
}
