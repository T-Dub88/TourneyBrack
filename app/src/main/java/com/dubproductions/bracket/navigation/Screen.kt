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
    object Loading: Screen(
        route = "loading",
        label = "Loading"
    )
    object Login: Screen(
        route = "login",
        label = "Login"
    )
    object Registration: Screen(
        route = "registration",
        label = "Registration"
    )
    object Home: Screen(
        route = "home",
        label = "Home",
        icon = Icons.Filled.Home
    )
    object Hosting: Screen(
        route = "hosting",
        label = "Hosting",
        icon = Icons.Filled.EventNote
    )
    object Participating: Screen(
        route = "participating",
        label = "Participating",
        icon = Icons.Filled.Event
    )
    object Settings: Screen(
        route = "settings",
        label = "Settings",
        icon = Icons.Filled.Settings
    )
}
