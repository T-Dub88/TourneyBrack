package com.dubproductions.bracket.ui.main.hosting.participant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dubproductions.bracket.data.Match
import com.dubproductions.bracket.data.MatchStatus
import com.dubproductions.bracket.data.Participant

@Composable
fun ParticipantMatchesScreen(
    participantList: List<Participant>,
    matchList: List<Match>,
    declareWinner: (winnerId: String?, roundNum: Int, matchId: String) -> Unit
) {

    var showDeclareDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedWinnerId: String? by rememberSaveable {
        mutableStateOf(null)
    }
    var selectedMatchId by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Participant Matches")
        
        if (showDeclareDialog) {
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
                onDismissRequest = { showDeclareDialog = false },
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

                            showDeclareDialog = false
                        }
                    ) {
                        Text(text = "Ok")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeclareDialog = false }) {
                        Text(text = "Cancel")
                    }
                },

            )

        }
        
        LazyColumn(
            contentPadding = PaddingValues(8.dp)
        ) {
           items(matchList) { match ->
                MatchCard(
                    match = match,
                    winnerClickEnabled = match.status == MatchStatus.PENDING.status,
                    getPlayerInfo = { playerId ->
                        participantList.find { it.userId == playerId } ?: Participant()
                    },
                    setWinnerClick = { winnerId ->
                        selectedWinnerId = winnerId
                        selectedMatchId = match.matchId
                        showDeclareDialog = true
                    }
                )
            }
        }
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                    .width(2.dp)
                    .fillMaxHeight(),
                color = MaterialTheme.colorScheme.onPrimaryContainer
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

private fun setPlayerMatchResult(
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
