package com.dubproductions.bracket.hosting.presentation.participantmatches

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dubproductions.bracket.R
import com.dubproductions.bracket.core.domain.model.match.Match
import com.dubproductions.bracket.core.domain.model.participant.Participant
import com.dubproductions.bracket.core.presentation.components.MatchCard
import com.dubproductions.bracket.hosting.presentation.components.DeclareWinnerDialog
import com.dubproductions.bracket.hosting.presentation.components.EditMatchDialog
import com.dubproductions.bracket.core.presentation.components.ReusableDialog
import com.dubproductions.bracket.core.domain.model.match.MatchStatus
import com.dubproductions.bracket.core.domain.model.tournament.TournamentStatus

@Composable
fun ParticipantMatchesScreen(
    participantList: List<Participant>,
    matchList: List<Match>,
    tournamentStatus: String,
    declareWinner: (winnerId: String?, roundNum: Int, match: Match) -> Unit,
    editMatch: (String, Int) -> Unit
) {
    var showDeclareDialog by rememberSaveable {
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (showDeclareDialog) {
            DeclareWinnerDialog(
                selectedWinnerId = selectedWinnerId,
                selectedMatchId = selectedMatchId,
                participantList = participantList,
                matchList = matchList,
                changeDialogVisibility = {
                    showDeclareDialog = it
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
                    editMatch(selectedMatchId, matchList.find { it.matchId == selectedMatchId }!!.roundNum)
                }
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(8.dp)
        ) {
           items(matchList) { match ->
                MatchCard(
                    match = match,
                    winnerClickEnabled = match.status == MatchStatus.PENDING.statusString,
                    getPlayerInfo = { playerId ->
                        participantList.find { it.userId == playerId } ?: Participant()
                    },
                    setWinnerClick = { winnerId ->
                        when (tournamentStatus) {
                            TournamentStatus.PLAYING.statusString,
                            TournamentStatus.COMPLETE_ROUNDS.statusString -> {
                                selectedWinnerId = winnerId
                                selectedMatchId = match.matchId
                                showDeclareDialog = true
                            }
                            else -> {
                                showNeedPlayingDialog = true
                            }
                        }
                    },
                    onEditClick = {
                        if (tournamentStatus != TournamentStatus.FINALIZED.statusString) {
                            selectedMatchId = match.matchId
                            showMatchEditDialog = true
                        }
                    }
                )
            }
        }
    }
}
