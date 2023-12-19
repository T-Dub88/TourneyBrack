package com.dubproductions.bracket.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.dubproductions.bracket.R
import com.dubproductions.bracket.Type
import com.dubproductions.bracket.Validation
import com.dubproductions.bracket.navigation.Screen
import com.dubproductions.bracket.ui.OnboardingButton
import com.dubproductions.bracket.ui.OnboardingTextField
import com.dubproductions.bracket.ui.ReusableDialog
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun RegistrationScreen(
    userViewModel: UserViewModel,
    mainNavController: NavHostController
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

    // Fields enable or disable state
    var enabled by rememberSaveable { mutableStateOf(true) }

    // Dialog visibility
    var showRegistrationFailureDialog by rememberSaveable { mutableStateOf(false) }

    Column {

        Text(text = stringResource(id = R.string.registration))

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
            enabled = enabled
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
            enabled = enabled
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
            enabled = enabled
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
            enabled = enabled
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
            enabled = enabled
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
            enabled = enabled
        )

        // Registration button
        OnboardingButton(
            whenClicked = {
                enabled = false
                emailError = verifyField(
                    text = emailText,
                    type = Type.EMAIL,
                    validation = Validation
                )
                usernameError = verifyField(
                    text = usernameText,
                    type = Type.EMPTY,
                    validation = Validation
                )
                firstNameError = verifyField(
                    text = firstNameText,
                    type = Type.EMPTY,
                    validation = Validation
                )
                lastNameError = verifyField(
                    text = lastNameText,
                    type = Type.EMPTY,
                    validation = Validation
                )
                passwordError = verifyField(
                    text = passwordText,
                    type = Type.PASSWORD,
                    extraText = passwordConfirmText,
                    validation = Validation
                )
                if (!emailError && !usernameError && !firstNameError && !lastNameError && !passwordError) {
                    userViewModel.registerUser(
                        email = emailText,
                        password = passwordText,
                        firstName = firstNameText,
                        lastName = lastNameText,
                        username = usernameText
                    ) {
                        enabled = true
                        if (it) {
                            mainNavController.navigate(Screen.Home.route) {
                                popUpTo(mainNavController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                            }
                        } else {
                            showRegistrationFailureDialog = true
                        }
                    }
                } else {
                    enabled = true
                }
            },
            buttonText = stringResource(id = R.string.register),
            enabled = enabled
        )

        // Dialog to tell user registration has failed.
        when {
            showRegistrationFailureDialog -> {
                ReusableDialog(
                    titleText = stringResource(id = R.string.registration_not_successful),
                    contentText = stringResource(id = R.string.registration_failure_message),
                    icon = Icons.Outlined.Error,
                    dismissDialog = { showRegistrationFailureDialog = false }
                )
            }
        }

    }
}

private fun verifyField(
    text: String,
    type: Type,
    validation: Validation,
    extraText: String? = null
): Boolean {
    return !when (type) {
        Type.EMPTY -> {
            validation.isFieldEmpty(text)
        }
        Type.PASSWORD -> {
            if (!extraText.isNullOrEmpty()) {
                validation.passwordsMatch(
                    password = text,
                    confirmPassword = extraText
                )
            } else {
                false
            }
        }
        Type.EMAIL -> {
            validation.isEmailValid(text)
        }
    }
}
