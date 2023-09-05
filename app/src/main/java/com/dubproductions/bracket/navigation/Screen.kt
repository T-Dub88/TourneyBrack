package com.dubproductions.bracket.navigation

sealed class Screen(val route: String) {
    object Login: Screen(route = "login")
    object Registration: Screen(route = "registration")
    object Home: Screen(route = "home")
}
