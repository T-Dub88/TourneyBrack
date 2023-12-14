package com.dubproductions.bracket.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dubproductions.bracket.navigation.Map
import com.dubproductions.bracket.navigation.Screen
import com.dubproductions.bracket.viewmodel.AppViewModel
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun LoadingScreen(
    navHostController: NavHostController,
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
            "home" -> {
                navHostController.navigate(Map.Main.route) {
                    popUpTo(Map.Onboarding.route) {
                        inclusive = true
                    }
                }
            }
            else -> {
                navHostController.navigate(navState.route) {
                    popUpTo(Screen.Loading.route) {
                        inclusive = true
                    }
                }
            }
        }
    }

    Text(text = "Loading Screen")
    
}
