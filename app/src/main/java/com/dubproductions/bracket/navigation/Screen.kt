package com.dubproductions.bracket.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    data object Login: Screen(
        route = "login",
        label = "Login"
    )
    data object Registration: Screen(
        route = "registration",
        label = "Registration"
    )
    data object Home: Screen(
        route = "home",
        label = "Home"
    )
    data object Hosting: Screen(
        route = "hosting",
        label = "Hosting"
    )
    data object Participating: Screen(
        route = "participating",
        label = "Participating"
    )
    data object Settings: Screen(
        route = "settings",
        label = "Settings"
    )

    data object TournamentCreation: Screen(
        route = "tournament_creation",
        label = "Tournament Creation",
    )

    data object EditTournament: Screen(
        route = "edit_tournament",
        label = "Edit Tournament"
    )

    data object Bracket: Screen(
        route = "bracket",
        label = "Bracket"
    )

    data object Participants: Screen(
        route = "participants",
        label = "Participants"
    )

    data object ParticipantMatches: Screen(
        route = "participant_matches",
        label = "Player Matches"
    )

}
