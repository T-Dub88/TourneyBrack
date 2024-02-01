package com.dubproductions.bracket.data.ui_state

data class LoginUIState(
    var enable: Boolean = true,
    var showPasswordResetSuccessDialog: Boolean = false,
    var showPasswordResetFailureDialog: Boolean = false,
    var showLoginFailureDialog: Boolean = false
)
