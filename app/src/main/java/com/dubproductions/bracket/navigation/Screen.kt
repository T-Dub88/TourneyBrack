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
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    data object Hosting: Screen(
        route = "hosting",
        label = "Hosting",
        selectedIcon = Icons.Filled.EventNote,
        unselectedIcon = Icons.Outlined.EventNote
    )
    data object Participating: Screen(
        route = "participating",
        label = "Participating",
        selectedIcon = Icons.Filled.Event,
        unselectedIcon = Icons.Outlined.Event
    )
    data object Settings: Screen(
        route = "settings",
        label = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
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

}
