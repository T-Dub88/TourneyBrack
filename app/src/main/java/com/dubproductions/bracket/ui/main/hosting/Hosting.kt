package com.dubproductions.bracket.ui.main.hosting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dubproductions.bracket.R
import com.dubproductions.bracket.data.Tournament
import com.dubproductions.bracket.navigation.Screen
import com.dubproductions.bracket.ui.TournamentSummaryCard
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun HostingScreen(
    userViewModel: UserViewModel,
    hostingNavController: NavHostController
) {

    val hostingTournamentList by userViewModel.hostingTournamentList.collectAsStateWithLifecycle()

    HostingScreenContent(
        hostingTournamentList = hostingTournamentList,
        floatingActionButtonClick = {
            hostingNavController.navigate(Screen.TournamentCreation.route)
        },
        tournamentCardClick = {
            // Todo: Set action for clicking existing tournament
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostingScreenContent(
    hostingTournamentList: MutableList<Tournament>,
    floatingActionButtonClick: () -> Unit,
    tournamentCardClick: () -> Unit
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
            Text(text = "Currently Hosting:")
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp)
                    .padding(horizontal = 8.dp)
            ) {
                items(hostingTournamentList) { tournament ->
                    TournamentSummaryCard(
                        tournament = tournament,
                        onPress = { tournamentCardClick() },
                        tapCardInstruction = stringResource(id = R.string.update_tournament)
                    )
                }
            }
        }
    }
}
