package com.dubproductions.bracket.navigation

sealed class Map(val route: String) {
    object Onboarding: Map(route = "onboarding")
    object Main: Map(route = "main")
}
