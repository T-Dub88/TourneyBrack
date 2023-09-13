package com.dubproductions.bracket.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.dubproductions.bracket.navigation.Screen
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun LoadingScreen(
    navHostController: NavHostController,
    userViewModel: UserViewModel
) {
    userViewModel.userLoggedIn { success ->
        if (success) {
            navHostController.navigate(Screen.Home.route) {
                popUpTo(Screen.Loading.route) {
                    inclusive = true
                }
            }
        } else {
            navHostController.navigate(Screen.Login.route) {
                popUpTo(Screen.Loading.route) {
                    inclusive = true
                }
            }
        }
    }
    
    Text(text = "Loading Screen")
    
}
