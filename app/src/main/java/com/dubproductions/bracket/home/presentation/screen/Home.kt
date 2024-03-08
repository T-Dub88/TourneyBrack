package com.dubproductions.bracket.home.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dubproductions.bracket.R
import com.dubproductions.bracket.core.domain.model.tournament.Tournament
import com.dubproductions.bracket.core.presentation.components.TournamentSummaryCard

@Composable
fun HomeScreen(
    username: String,
    tournamentList: List<Tournament>,
    cardPressed: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(
                id = R.string.greeting,
                username
            )
        )
        Text(text = stringResource(id = R.string.tournament_history))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp)
                .padding(horizontal = 8.dp)
        ) {
            items(tournamentList) { tournament ->
                TournamentSummaryCard(
                    tournament = tournament,
                    onPress = { cardPressed(tournament.tournamentId) },
                    tapCardInstruction = stringResource(id = R.string.more_info_card)
                )
            }
        }
    }
}
