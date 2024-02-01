package com.dubproductions.bracket.ui.main.hosting.participant

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dubproductions.bracket.data.Match
import com.dubproductions.bracket.data.status.MatchStatus
import com.dubproductions.bracket.data.Participant
import com.dubproductions.bracket.ui.components.DeclareWinnerDialog
import com.dubproductions.bracket.ui.components.MatchCard

@Composable
fun ParticipantMatchesScreen(
    participantList: List<Participant>,
    matchList: List<Match>,
    declareWinner: (winnerId: String?, roundNum: Int, matchId: String) -> Unit
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
        
        LazyColumn(
            contentPadding = PaddingValues(8.dp)
        ) {
           items(matchList) { match ->
                MatchCard(
                    match = match,
                    winnerClickEnabled = match.status == MatchStatus.PENDING.status,
                    getPlayerInfo = { playerId ->
                        participantList.find { it.userId == playerId } ?: Participant()
                    },
                    setWinnerClick = { winnerId ->
                        selectedWinnerId = winnerId
                        selectedMatchId = match.matchId
                        showDeclareDialog = true
                    }
                )
            }
        }
    }
}


