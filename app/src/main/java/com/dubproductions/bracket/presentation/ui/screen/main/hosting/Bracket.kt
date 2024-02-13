package com.dubproductions.bracket.presentation.ui.screen.main.hosting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dubproductions.bracket.R
import com.dubproductions.bracket.domain.model.Match
import com.dubproductions.bracket.domain.model.Participant
import com.dubproductions.bracket.domain.model.Round
import com.dubproductions.bracket.domain.model.Tournament
import com.dubproductions.bracket.presentation.ui.components.MatchCard
import com.dubproductions.bracket.presentation.ui.components.dialogs.DeclareWinnerDialog
import com.dubproductions.bracket.presentation.ui.components.dialogs.EditMatchDialog
import com.dubproductions.bracket.presentation.ui.components.dialogs.ReusableDialog
import com.dubproductions.bracket.utils.status.MatchStatus
import com.dubproductions.bracket.utils.status.TournamentStatus

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BracketScreen(
    tournament: Tournament,
    declareWinner: (winnerId: String?, roundNum: Int, match: Match) -> Unit,
    editMatch: (String, Int) -> Unit
) {
    var selectedRoundIndex: Int by rememberSaveable {
        mutableIntStateOf(0)
    }

    val pagerState = rememberPagerState {
        if (tournament.roundIds.isEmpty()) {
            1
        } else {
            tournament.roundIds.size
        }
    }

    var showDeclareWinnerDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedWinnerId: String? by rememberSaveable {
        mutableStateOf(null)
    }
    var selectedMatchId by rememberSaveable {
        mutableStateOf("")
    }
    var showNeedPlayingDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showMatchEditDialog by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(selectedRoundIndex) {
        pagerState.animateScrollToPage(selectedRoundIndex)
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedRoundIndex = pagerState.currentPage
        }
    }

    if (showDeclareWinnerDialog) {
        DeclareWinnerDialog(
            selectedWinnerId = selectedWinnerId,
            selectedMatchId = selectedMatchId,
            participantList = tournament.participants,
            matchList = tournament.rounds[selectedRoundIndex].matches,
            changeDialogVisibility = {
                showDeclareWinnerDialog = it
            },
            declareWinner = declareWinner
        )
    }

    if (showNeedPlayingDialog) {
        ReusableDialog(
            titleText = stringResource(id = R.string.not_playing),
            contentText = stringResource(id = R.string.not_playing_warning),
            icon = Icons.Default.ErrorOutline,
            dismissDialog = {
                showNeedPlayingDialog = false
            }
        )
    }

    if (showMatchEditDialog) {
        EditMatchDialog(
            hideDialog = {
                showMatchEditDialog = false
            },
            editMatch = {
                showMatchEditDialog = false
                editMatch(selectedMatchId, selectedRoundIndex + 1)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedRoundIndex,
        ) {
            tournament.rounds.forEach { round ->
                Tab(
                    selected = true,
                    onClick = {
                        selectedRoundIndex = round.roundNum - 1
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.round, round.roundNum),
                            fontSize = 16.sp
                        )
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) { index ->
            LazyColumn(
                contentPadding = PaddingValues(8.dp)
            ) {
                items(tournament.rounds[index].matches) { match ->
                    MatchCard(
                        match = match,
                        winnerClickEnabled = match.status == MatchStatus.PENDING.statusString,
                        getPlayerInfo = { playerId ->
                            tournament.participants.find { it.userId == playerId } ?: Participant()
                        },
                        setWinnerClick = { winnerId ->
                            if (tournament.status == TournamentStatus.PLAYING.statusString) {
                                selectedWinnerId = winnerId
                                selectedMatchId = match.matchId
                                showDeclareWinnerDialog = true
                            } else {
                                showNeedPlayingDialog = true
                            }
                        },
                        onEditClick = {
                            selectedMatchId = match.matchId
                            showMatchEditDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBracketScreen() {
    BracketScreen(
        tournament = Tournament(
            rounds = mutableListOf(
                Round(
                    roundNum = 1
                ),
                Round(roundNum = 2),
                Round(roundNum = 3),
                Round(roundNum = 4),
                Round(roundNum = 5),
                Round(roundNum = 6),
            )
        ),
        declareWinner = { _, _, _ -> },
        editMatch = { _, _ -> }
    )
}
