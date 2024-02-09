package com.dubproductions.bracket.presentation.ui.screen.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.dubproductions.bracket.presentation.ui.components.OnboardingButton
import com.dubproductions.bracket.presentation.ui.components.OnboardingTextField
import com.dubproductions.bracket.presentation.ui.components.dialogs.ReusableDialog
import com.dubproductions.bracket.presentation.ui.state.RegistrationUIState
import com.dubproductions.bracket.utils.Type
import com.dubproductions.bracket.utils.Validation

@Composable
fun RegistrationScreen(
    uiState: RegistrationUIState,
    dismissDialog: () -> Unit,
    registrationClicked: (
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        username: String
    ) -> Unit
) {
    // Field texts
    var emailText by rememberSaveable { mutableStateOf("") }
    var usernameText by rememberSaveable { mutableStateOf("") }
    var firstNameText by rememberSaveable { mutableStateOf("") }
    var lastNameText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }
    var passwordConfirmText by rememberSaveable { mutableStateOf("") }

    // Password boolean
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Field error states
    var emailError by rememberSaveable { mutableStateOf(false) }
    var usernameError by rememberSaveable { mutableStateOf(false) }
    var firstNameError by rememberSaveable { mutableStateOf(false) }
    var lastNameError by rememberSaveable { mutableStateOf(false) }
    var passwordError by rememberSaveable { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState)
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
            enabled = uiState.uiEnabled,
            capitalize = false
        )

        // Text field for username
        OnboardingTextField(
            text = usernameText,
            whenChanged = { newText ->
                usernameText = newText
                if (usernameError) {
                    usernameError = false
                }
            },
            hint = stringResource(id = R.string.username),
            leadingIcon = Icons.Filled.PersonOutline,
            trailingIcon = Icons.Filled.Clear,
            trailingIconOnClick = { usernameText = "" },
            visualTransformation = VisualTransformation.None,
            error = usernameError,
            errorText = stringResource(id = R.string.username_not_valid),
            enabled = uiState.uiEnabled,
            capitalize = false
        )

        // Text field for first name
        OnboardingTextField(
            text = firstNameText,
            whenChanged = { newText ->
                firstNameText = newText
                if (firstNameError) {
                    firstNameError = false
                }
            },
            hint = stringResource(id = R.string.first_name),
            leadingIcon = Icons.Filled.Person,
            trailingIcon = Icons.Filled.Clear,
            trailingIconOnClick = { firstNameText = "" },
            visualTransformation = VisualTransformation.None,
            error = firstNameError,
            errorText = stringResource(id = R.string.cannot_be_blank),
            enabled = uiState.uiEnabled,
            capitalize = true
        )

        // Text field for last name
        OnboardingTextField(
            text = lastNameText,
            whenChanged = { newText ->
                lastNameText = newText
                if (lastNameError) {
                    lastNameError = false
                }
            },
            hint = stringResource(id = R.string.last_name),
            leadingIcon = Icons.Filled.Person,
            trailingIcon = Icons.Filled.Clear,
            trailingIconOnClick = { lastNameText = "" },
            visualTransformation = VisualTransformation.None,
            error = lastNameError,
            errorText = stringResource(id = R.string.cannot_be_blank),
            enabled = uiState.uiEnabled,
            capitalize = true
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
            errorText = if (passwordText != passwordConfirmText) {
                stringResource(id = R.string.passwords_match)
            } else {
                stringResource(id = R.string.cannot_be_blank)
            },
            enabled = uiState.uiEnabled,
            capitalize = false
        )

        // Text field for confirm password
        OnboardingTextField(
            text = passwordConfirmText,
            whenChanged = { newText ->
                passwordConfirmText = newText
                if (passwordError) {
                    passwordError = false
                }
            },
            hint = stringResource(id = R.string.confirm_password),
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
            errorText = if (passwordText != passwordConfirmText) {
                stringResource(id = R.string.passwords_match)
            } else {
                stringResource(id = R.string.cannot_be_blank)
            },
            enabled = uiState.uiEnabled,
            capitalize = false
        )

        // Registration button
        OnboardingButton(
            whenClicked = {
                emailError = verifyField(
                    text = emailText,
                    type = Type.EMAIL
                )
                usernameError = verifyField(
                    text = usernameText,
                    type = Type.EMPTY
                )
                firstNameError = verifyField(
                    text = firstNameText,
                    type = Type.EMPTY
                )
                lastNameError = verifyField(
                    text = lastNameText,
                    type = Type.EMPTY
                )
                passwordError = verifyField(
                    text = passwordText,
                    type = Type.PASSWORD,
                    extraText = passwordConfirmText
                )
                if (!emailError && !usernameError && !firstNameError && !lastNameError && !passwordError) {
                    registrationClicked(
                        emailText,
                        passwordText,
                        firstNameText,
                        lastNameText,
                        usernameText
                    )
                }
            },
            buttonText = stringResource(id = R.string.register),
            enabled = uiState.uiEnabled
        )

        // Dialog to tell user registration has failed.
        when {
            uiState.displayRegistrationFailureDialog -> {
                ReusableDialog(
                    titleText = stringResource(id = R.string.registration_not_successful),
                    contentText = stringResource(id = R.string.registration_failure_message),
                    icon = Icons.Outlined.Error,
                    dismissDialog = dismissDialog
                )
            }
        }

    }
}

private fun verifyField(
    text: String,
    type: Type,
    extraText: String? = null
): Boolean {
    return !when (type) {
        Type.EMPTY -> {
            Validation.isFieldEmpty(text)
        }
        Type.PASSWORD -> {
            if (!extraText.isNullOrEmpty()) {
                Validation.passwordsMatch(
                    password = text,
                    confirmPassword = extraText
                )
            } else {
                false
            }
        }
        Type.EMAIL -> {
            Validation.isEmailValid(text)
        }
        else -> true
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen(
        uiState = RegistrationUIState(),
        dismissDialog = {  },
        registrationClicked = { _, _, _, _, _ -> }
    )
}
