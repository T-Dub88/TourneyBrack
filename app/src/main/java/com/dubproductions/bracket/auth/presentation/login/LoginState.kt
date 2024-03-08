package com.dubproductions.bracket.auth.presentation.login

data class LoginState(
    var enable: Boolean = true,
    var showPasswordResetSuccessDialog: Boolean = false,
    var showPasswordResetFailureDialog: Boolean = false,
    var showLoginFailureDialog: Boolean = false
)
