package com.dubproductions.bracket.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dubproductions.bracket.R
import com.dubproductions.bracket.data.Match
import com.dubproductions.bracket.data.Participant

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
            text = stringResource(id = R.string.round, match.round),
            modifier = Modifier
                .align(
                    alignment = Alignment.CenterHorizontally
                ),
            fontSize = 25.sp
        )
        Text(
            text = stringResource(
                id = R.string.match_status,
                match.status.replaceFirstChar { it.uppercase() }
            ),
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
                matchResult = stringResource(
                    id = setPlayerMatchResult(
                        tie = match.tie,
                        winnerId = match.winnerId,
                        playerId = playerOne.userId
                    )
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
                matchResult = stringResource(
                    id = setPlayerMatchResult(
                        tie = match.tie,
                        winnerId = match.winnerId,
                        playerId = playerOne.userId
                    )
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
            Text(text = stringResource(id = R.string.tie))
        }
    }
}

fun setPlayerMatchResult(
    tie: Boolean?,
    winnerId: String?,
    playerId: String
): Int {
    return if (tie == true) {
        R.string.tie
    } else if (winnerId == playerId) {
        R.string.win
    } else if (!winnerId.isNullOrEmpty()) {
        R.string.lose
    } else {
        R.string.pending
    }
}
