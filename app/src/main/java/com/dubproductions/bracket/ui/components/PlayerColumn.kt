package com.dubproductions.bracket.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import com.dubproductions.bracket.data.Participant

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
