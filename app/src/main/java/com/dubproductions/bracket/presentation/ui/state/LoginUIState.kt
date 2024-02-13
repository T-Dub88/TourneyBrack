package com.dubproductions.bracket.presentation.ui.state

data class LoginUIState(
    var enable: Boolean = true,
    var showPasswordResetSuccessDialog: Boolean = false,
    var showPasswordResetFailureDialog: Boolean = false,
    var showLoginFailureDialog: Boolean = false
)
