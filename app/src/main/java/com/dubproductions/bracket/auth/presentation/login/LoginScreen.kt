package com.dubproductions.bracket.auth.presentation.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.dubproductions.bracket.R
import com.dubproductions.bracket.core.utils.Type
import com.dubproductions.bracket.core.utils.Validation
import com.dubproductions.bracket.auth.presentation.components.OnboardingButton
import com.dubproductions.bracket.auth.presentation.components.OnboardingTextField
import com.dubproductions.bracket.core.presentation.components.ReusableDialog

@Composable
fun LoginScreen(
    uiState: LoginState,
    loginClick: (String, String) -> Unit,
    registrationClick: () -> Unit,
    forgotPasswordClick: (String) -> Unit,
    dismissDialog: () -> Unit
) {
    // Text field texts
    var emailText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }

    // Password visibility
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Error states
    var emailError by rememberSaveable { mutableStateOf(false) }
    var passwordError by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Text field for email
        OnboardingTextField(
            text = emailText,
            whenChanged = { newText ->
                emailText = newText
                if (emailError) {
                    emailError = false
                }
            },
            hint = stringResource(id = R.string.email),
            leadingIcon = Icons.Filled.Email,
            trailingIcon = Icons.Filled.Clear,
            trailingIconOnClick = { emailText = "" },
            visualTransformation = VisualTransformation.None,
            error = emailError,
            errorText = stringResource(id = R.string.email_not_valid),
            enabled = uiState.enable,
            capitalize = false
        )

        // Text field for password
        OnboardingTextField(
            text = passwordText,
            whenChanged = { newText ->
                passwordText = newText
                if (passwordError) {
                    passwordError = false
                }
            },
            hint = stringResource(id = R.string.password),
            leadingIcon = Icons.Filled.Lock,
            trailingIcon = if (passwordVisible) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            },
            trailingIconOnClick = { passwordVisible = !passwordVisible },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            error = passwordError,
            errorText = stringResource(id = R.string.password_needed),
            enabled = uiState.enable,
            capitalize = false
        )

        // Button to log user in
        OnboardingButton(
            whenClicked = {
                emailError = validateFields(
                    text = emailText,
                    type = Type.EMAIL
                )
                passwordError = validateFields(
                    text = passwordText,
                    type = Type.EMPTY
                )

                if (!passwordError && !emailError) {
                    loginClick(emailText, passwordText)
                }

            },
            buttonText = stringResource(id = R.string.login),
            enabled = uiState.enable
        )

        // Button to navigate to the registration screen
        OnboardingButton(
            whenClicked = registrationClick,
            buttonText = stringResource(id = R.string.registration),
            enabled = uiState.enable
        )

        // Button for password reset
        OnboardingButton(
            whenClicked = {
                emailError = validateFields(
                    text = emailText,
                    type = Type.EMAIL
                )

                if (!emailError) {
                    forgotPasswordClick(emailText)
                }

            },
            buttonText = stringResource(id = R.string.forgot_password),
            enabled = uiState.enable
        )

        // Dialog to tell user password email has sent.
        when {
            uiState.showPasswordResetSuccessDialog -> {
                ReusableDialog(
                    titleText = stringResource(id = R.string.email_sent),
                    contentText = stringResource(id = R.string.password_email_sent_message),
                    icon = Icons.Outlined.Email,
                    dismissDialog = { dismissDialog() }
                )
            }
        }

        // Dialog to tell user login has failed.
        when {
            uiState.showLoginFailureDialog -> {
                ReusableDialog(
                    titleText = stringResource(id = R.string.login_failed),
                    contentText = stringResource(id = R.string.login_failed_message),
                    icon = Icons.Outlined.Error,
                    dismissDialog = { dismissDialog() }
                )
            }
        }

        // Dialog to tell user password email not sent.
        when {
            uiState.showPasswordResetFailureDialog -> {
                ReusableDialog(
                    titleText = stringResource(id = R.string.password_email_not_sent),
                    contentText = stringResource(id = R.string.password_email_not_sent_message),
                    icon = Icons.Outlined.Error,
                    dismissDialog = { dismissDialog() }
                )
            }
        }
    }
}

private fun validateFields(
    text: String,
    type: Type,
): Boolean {
    return !when (type) {
        Type.EMAIL -> {
            Validation.isEmailValid(text)
        }
        Type.EMPTY -> {
            Validation.isFieldEmpty(text)
        } else -> {
            false
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        uiState = LoginState(),
        loginClick = {_, _ -> },
        registrationClick = {},
        forgotPasswordClick = {},
        dismissDialog = {}
    )
}
