package com.dubproductions.bracket.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    preLoginNavController: NavHostController,
    mainNavHostController: NavHostController
) {

    val coroutineScope = rememberCoroutineScope()

    // Text field texts
    var emailText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }

    // Password visibility
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Error states
    var emailError by rememberSaveable { mutableStateOf(false) }
    var passwordError by rememberSaveable { mutableStateOf(false) }

    // Fields enable or disable state
    var enabled by rememberSaveable { mutableStateOf(true) }

    // Dialog visibility
    var showPasswordSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showPasswordFailureDialog by rememberSaveable { mutableStateOf(false) }
    var showLoginFailureDialog by rememberSaveable { mutableStateOf(false) }

    Column {
        Text(text = stringResource(id = R.string.login))

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
            enabled = enabled
        )

        // Button to log user in
        OnboardingButton(
            whenClicked = {
                enabled = false
                emailError = validateFields(
                    text = emailText,
                    type = Type.EMAIL,
                    validation = Validation
                )
                passwordError = validateFields(
                    text = passwordText,
                    type = Type.EMPTY,
                    validation = Validation
                )

                if (!passwordError || !emailError) {
                    userViewModel.loginUser(
                        email = emailText,
                        password = passwordText
                    ) {
                        enabled = true
                        if (it) {
                            mainNavHostController.navigate(Screen.Home.route) {
                                popUpTo(mainNavHostController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                            }
                        } else {
                            showLoginFailureDialog = true
                        }
                    }
                } else {
                    enabled = true
                }

            },
            buttonText = stringResource(id = R.string.login),
            enabled = enabled
        )

        // Button to navigate to the registration screen
        OnboardingButton(
            whenClicked = { preLoginNavController.navigate(Screen.Registration.route) },
            buttonText = stringResource(id = R.string.registration),
            enabled = enabled
        )

        // Button for password reset
        OnboardingButton(
            whenClicked = {
                enabled = false
                emailError = validateFields(
                    text = emailText,
                    type = Type.EMAIL,
                    validation = Validation
                )

                if (!emailError) {
                    coroutineScope.launch {
                        val resetResult = userViewModel.resetPassword(emailText)
                        enabled = true
                        if (resetResult) {
                            showPasswordSuccessDialog = true
                        } else {
                            showPasswordFailureDialog = true
                        }
                    }
                } else {
                    enabled = true
                }

            },
            buttonText = stringResource(id = R.string.forgot_password),
            enabled = enabled
        )

        // Dialog to tell user password email has sent.
        when {
            showPasswordSuccessDialog -> {
                ReusableDialog(
                    titleText = stringResource(id = R.string.email_sent),
                    contentText = stringResource(id = R.string.password_email_sent_message),
                    icon = Icons.Outlined.Email,
                    dismissDialog = { showPasswordSuccessDialog = false }
                )
            }
        }


        // Dialog to tell user login has failed.
        when {
            showLoginFailureDialog -> {
                ReusableDialog(
                    titleText = stringResource(id = R.string.login_failed),
                    contentText = stringResource(id = R.string.login_failed_message),
                    icon = Icons.Outlined.Error,
                    dismissDialog = { showLoginFailureDialog = false }
                )
            }
        }

        // Dialog to tell user password email not sent.
        when {
            showPasswordFailureDialog -> {
                ReusableDialog(
                    titleText = stringResource(id = R.string.password_email_not_sent),
                    contentText = stringResource(id = R.string.password_email_not_sent_message),
                    icon = Icons.Outlined.Error,
                    dismissDialog = { showPasswordFailureDialog = false }
                )
            }
        }
    }
}

private fun validateFields(
    text: String,
    type: Type,
    validation: Validation
): Boolean {
    return !when (type) {
        Type.EMAIL -> {
            validation.isEmailValid(text)
        }
        Type.EMPTY -> {
            validation.isFieldEmpty(text)
        } else -> {
            false
        }
    }
}
