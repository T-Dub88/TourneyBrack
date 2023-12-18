package com.dubproductions.bracket.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector? = null
) {
    data object Loading: Screen(
        route = "loading",
        label = "Loading"
    )
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
        icon = Icons.Filled.Home
    )
    data object Hosting: Screen(
        route = "hosting",
        label = "Hosting",
        icon = Icons.Filled.EventNote
    )
    data object Participating: Screen(
        route = "participating",
        label = "Participating",
        icon = Icons.Filled.Event
    )
    data object Settings: Screen(
        route = "settings",
        label = "Settings",
        icon = Icons.Filled.Settings
    )
}
