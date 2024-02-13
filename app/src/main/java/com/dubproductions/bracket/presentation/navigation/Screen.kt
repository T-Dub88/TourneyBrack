package com.dubproductions.bracket.presentation.navigation

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
