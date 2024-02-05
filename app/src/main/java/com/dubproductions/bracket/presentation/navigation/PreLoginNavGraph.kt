package com.dubproductions.bracket.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.presentation.ui.screen.onboarding.LoginScreen
import com.dubproductions.bracket.presentation.ui.screen.onboarding.RegistrationScreen
import com.dubproductions.bracket.presentation.viewmodel.LoginViewModel
import com.dubproductions.bracket.presentation.viewmodel.RegistrationViewModel
import kotlinx.coroutines.launch

fun NavGraphBuilder.preLoginNavGraph(
    navigateToHomeScreen: () -> Unit,
    navigateToRegistrationScreen: () -> Unit
) {
    navigation(
        startDestination = Screen.Login.route,
        route = Map.PreLogin.route
    ) {
        loginScreen(
            navigateToRegistrationScreen = navigateToRegistrationScreen,
            navigateToHomeScreen = navigateToHomeScreen
        )
        registrationScreen(navigateToHomeScreen)
    }
}

fun NavGraphBuilder.loginScreen(
    navigateToHomeScreen: () -> Unit,
    navigateToRegistrationScreen: () -> Unit
) {
    composable(
        route = Screen.Login.route
    ) {
        val loginViewModel: LoginViewModel = hiltViewModel()
        val uiState by loginViewModel.loginUIState.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()

        LoginScreen(
            loginClick = { email, password ->
                coroutineScope.launch {
                    val result = loginViewModel.loginUser(email, password)
                    if (result) {
                        navigateToHomeScreen()
                    } else {
                        loginViewModel.showLoginFailureDialog()
                    }
                }
            },
            registrationClick = { navigateToRegistrationScreen() },
            forgotPasswordClick = { email ->
                loginViewModel.resetPassword(email)
            },
            dismissDialog = { loginViewModel.dismissLoginDialogs() },
            uiState = uiState
        )
    }
}

fun NavGraphBuilder.registrationScreen(
    navigateToHomeScreen: () -> Unit
) {
    composable(
        route = Screen.Registration.route
    ) {
        val registrationViewModel: RegistrationViewModel = hiltViewModel()
        val uiState by registrationViewModel.uiState.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()

        RegistrationScreen(
            uiState = uiState,
            dismissDialog = { registrationViewModel.hideDialog() },
            registrationClicked = { email, password, firstName, lastName, username ->
                coroutineScope.launch {
                    val result = registrationViewModel.registerUser(
                        email = email,
                        password = password,
                        firstName = firstName,
                        lastName = lastName,
                        username = username
                    )

                    if (result) {
//                        navigateToHomeScreen()
                    } else {
                        registrationViewModel.showDialog()
                    }

                }
            }
        )
    }
}
