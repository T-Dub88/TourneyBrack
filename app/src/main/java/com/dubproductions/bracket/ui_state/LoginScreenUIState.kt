package com.dubproductions.bracket.ui_state

data class LoginScreenUIState(
    var enable: Boolean = true,
    var showPasswordResetSuccessDialog: Boolean = false,
    var showPasswordResetFailureDialog: Boolean = false,
    var showLoginFailureDialog: Boolean = false
)
