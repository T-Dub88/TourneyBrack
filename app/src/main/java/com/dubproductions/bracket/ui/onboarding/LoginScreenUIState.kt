package com.dubproductions.bracket.ui.onboarding

data class LoginScreenUIState(
    var enable: Boolean = true,
    var showPasswordResetSuccessDialog: Boolean = false,
    var showPasswordResetFailureDialog: Boolean = false,
    var showLoginFailureDialog: Boolean = false,
    var title: String = "Login"
)
