package com.dubproductions.bracket.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dubproductions.bracket.R
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.utils.TournamentHousekeeping.setNumberOfRounds
import java.text.SimpleDateFormat
import java.util.Locale

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
                        if (tournament.timeEnded != null) {
                            formatDateTime(tournament.timeEnded)
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

fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM-dd-yy HH:mm", Locale.getDefault())
    return sdf.format(timestamp)
}
