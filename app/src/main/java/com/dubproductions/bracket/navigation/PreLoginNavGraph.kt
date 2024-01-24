package com.dubproductions.bracket.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dubproductions.bracket.ui.onboarding.LoginScreen
import com.dubproductions.bracket.ui.onboarding.RegistrationScreen
import com.dubproductions.bracket.viewmodel.LoginViewModel
import com.dubproductions.bracket.viewmodel.RegistrationViewModel
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
                loginViewModel.disableLoginScreenUI()
                coroutineScope.launch {
                    val result = loginViewModel.loginUser(email, password)
                    loginViewModel.enableLoginScreenUI()
                    if (result) {
                        navigateToHomeScreen()
                    } else {
                        loginViewModel.showLoginFailureDialog()
                    }
                }
            },
            registrationClick = { navigateToRegistrationScreen() },
            forgotPasswordClick = { email ->
                loginViewModel.disableLoginScreenUI()
                coroutineScope.launch {
                    val result = loginViewModel.resetPassword(email)
                    loginViewModel.enableLoginScreenUI()
                    if (result) {
                        loginViewModel.showPasswordResetSuccessDialog()
                    } else {
                        loginViewModel.showPasswordResetFailureDialog()
                    }
                }
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
                    registrationViewModel.disableUI()
                    val result = registrationViewModel.registerUser(
                        email = email,
                        password = password,
                        firstName = firstName,
                        lastName = lastName,
                        username = username
                    )
                    registrationViewModel.enableUI()
                    if (result) {
                        navigateToHomeScreen()
                    } else {
                        registrationViewModel.showDialog()
                    }
                }
            }
        )
    }
}
