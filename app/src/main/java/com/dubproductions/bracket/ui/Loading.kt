package com.dubproductions.bracket.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.dubproductions.bracket.navigation.Screen
import com.dubproductions.bracket.viewmodel.AppViewModel
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun LoadingScreen(
    mainNavController: NavHostController,
    preLoginNavHostController: NavHostController,
    userViewModel: UserViewModel,
    appViewModel: AppViewModel
) {
    val navState by appViewModel.currentNavState.collectAsStateWithLifecycle()

    userViewModel.userLoggedIn {
        if (it) {
            appViewModel.updateNavState(Screen.Home)
        } else {
            appViewModel.updateNavState(Screen.Login)
        }
    }

    LaunchedEffect(navState) {
        when (navState.route) {
            Screen.Home.route -> {
                mainNavController.navigate(navState.route) {
                    popUpTo(mainNavController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                }
            }
            else -> {
                preLoginNavHostController.navigate(navState.route) {
                    popUpTo(Screen.Loading.route) {
                        inclusive = true
                    }
                }
            }
        }
    }

    Text(text = "Loading Screen")
    
}
