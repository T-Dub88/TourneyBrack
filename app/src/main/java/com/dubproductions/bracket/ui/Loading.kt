package com.dubproductions.bracket.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.dubproductions.bracket.navigation.Screen

@Composable
fun LoadingScreen(
    navHostController: NavHostController
) {
    navHostController.navigate(Screen.Login.route) {
        popUpTo(Screen.Loading.route) {
            inclusive = true
        }
    }
}
