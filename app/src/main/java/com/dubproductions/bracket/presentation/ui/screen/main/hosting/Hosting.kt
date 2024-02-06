package com.dubproductions.bracket.presentation.ui.screen.main.hosting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dubproductions.bracket.R
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.presentation.ui.components.TournamentSummaryCard

@Composable
fun HostingScreen(
    hostingTournamentList: List<Tournament>,
    floatingActionButtonClick: () -> Unit,
    tournamentCardClick: (Tournament) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { floatingActionButtonClick() },
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
            }
        }
    ) {
        Column(
            Modifier.padding(it)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp)
                    .padding(horizontal = 8.dp)
            ) {
                items(hostingTournamentList) { tournament ->
                    TournamentSummaryCard(
                        tournament = tournament,
                        onPress = { tournamentCardClick(tournament) },
                        tapCardInstruction = stringResource(id = R.string.update_tournament)
                    )
                }
            }
        }
    }

}
