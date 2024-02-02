package com.dubproductions.bracket.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.dubproductions.bracket.R
import com.dubproductions.bracket.domain.model.Participant

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
            text = participant?.username ?: stringResource(id = R.string.bye),
            fontSize = 25.sp
        )
        Text(text = stringResource(id = R.string.match_result, matchResult))
        Button(
            onClick = { setWinnerClick(participant?.userId ?: "") },
            enabled = winnerClickEnabled
        ) {
            Text(text = stringResource(id = R.string.winner))
        }
    }
}
