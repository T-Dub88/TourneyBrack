package com.dubproductions.bracket.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dubproductions.bracket.R
import com.dubproductions.bracket.data.Participant
import com.dubproductions.bracket.data.Tournament
import com.dubproductions.bracket.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(userViewModel: UserViewModel) {

    val userInfo by userViewModel.user.collectAsStateWithLifecycle()
    val tournamentList by userViewModel.tournamentList.collectAsStateWithLifecycle()

    HomeScreenContent(
        userName = userInfo.username.toString(),
        tournamentList = tournamentList
    )

}

@Composable
fun HomeScreenContent(
    userName: String,
    tournamentList: MutableList<Tournament>
) {
    Column {
        Text(text = stringResource(
            id = R.string.greeting,
            userName)
        )
        Text(text = stringResource(id = R.string.tournament_history))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp)
                .padding(horizontal = 8.dp)
        ) {
            items(tournamentList) { tournament ->
                TournamentSummaryCard(tournament = tournament)
            }
        }
    }
}

@Composable
fun TournamentSummaryCard(tournament: Tournament) {
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(
                    text = stringResource(
                        id = R.string.tournament_name,
                        tournament.name.toString()
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.status,
                        tournament.status.toString()
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.rounds,
                        tournament.roundCount.toString()
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
                        tournament.type.toString()
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.started_date,
                        formatDateTime(tournament.timeStarted)
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.completed_date,
                        formatDateTime(tournament.timeCompleted)
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
            }
        }
    }
}

private fun formatDateTime(timestamp: Long?): String {
    val sdf = SimpleDateFormat("MM-dd-yy HH:mm", Locale.getDefault())
    return sdf.format(timestamp)
}

@Preview
@Composable
private fun TournamentCardPreview() {
    val tournament: Tournament = Tournament(
        name = "Tourney Classic",
        id = "djsjlkhfj",
        type = "Swiss",
        roundCount = 0,
        status = "Complete",
        participants = listOf(
            Participant(),
            Participant(),
            Participant(),
            Participant(),
            Participant(),
            Participant(),
            Participant(),
            Participant()
        )
    )
    tournament.setNumberOfRounds()
    tournament.timeStampStart()
    tournament.timeStampFinish()

    TournamentSummaryCard(tournament = tournament)
}
