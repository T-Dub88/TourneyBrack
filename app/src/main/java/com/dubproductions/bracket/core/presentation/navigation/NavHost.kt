package com.dubproductions.bracket.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.dubproductions.bracket.home.presentation.navigation.homeNavGraph
import com.dubproductions.bracket.hosting.presentation.navigation.hostingNavGraph
import com.dubproductions.bracket.participating.presentation.navigation.participatingNavGraph
import com.dubproductions.bracket.auth.presentation.navigation.preLoginNavGraph
import com.dubproductions.bracket.settings.presentation.navigation.settingsNavGraph


@Composable
fun NavHost(
    navController: NavHostController,
    loggedIn: Boolean,
    setLoggedInStatus: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = if (loggedIn) {
            Map.BottomBar.route
        } else {
            Map.PreLogin.route
        }
    ) {
        preLoginNavGraph(
            navigateToHomeScreen = {
                setLoggedInStatus(true)
                navController.navigate(Map.BottomBar.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                }
            },
            navigateToRegistrationScreen = {
                navController.navigate(Screen.Registration.route)
            }
        )
        bottomBarNavGraph(navController)
    }
}

fun NavGraphBuilder.bottomBarNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Map.Home.route,
        route = Map.BottomBar.route
    ) {
        homeNavGraph(navController)
        hostingNavGraph(navController)
        participatingNavGraph()
        settingsNavGraph()
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavHostController): T {
    val navGraphRoute = destination.parent?.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}
