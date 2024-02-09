package com.dubproductions.bracket.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dubproductions.bracket.R
import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.utils.status.MatchStatus

@Composable
fun MatchCard(
    match: Match,
    winnerClickEnabled: Boolean,
    getPlayerInfo: (String) -> Participant,
    setWinnerClick: (String?) -> Unit,
    onEditClick: () -> Unit
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.round, match.roundNum),
                fontSize = 25.sp,
                modifier = Modifier
                    .align(Alignment.Center)
            )

            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp),
                onClick = { if (match.status == MatchStatus.COMPLETE.statusString) onEditClick() },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            }
        }

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

            VerticalDivider(
               color = MaterialTheme.colorScheme.onSurface,
                thickness = Dp.Hairline
            )

            PlayerColumn(
                participant = playerTwo,
                matchResult = stringResource(
                    id = setPlayerMatchResult(
                        tie = match.tie,
                        winnerId = match.winnerId,
                        playerId = playerTwo?.userId
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
    playerId: String?
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

@Preview
@Composable
fun MatchCardPreview() {
    MatchCard(
        match = Match(),
        winnerClickEnabled = true,
        getPlayerInfo = { _ -> return@MatchCard Participant() },
        setWinnerClick = {},
        onEditClick = {}
    )
}
