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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavHostController
import com.dubproductions.bracket.Type
import com.dubproductions.bracket.Validation
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun RegistrationScreen(
    userViewModel: UserViewModel,
    navHostController: NavHostController,
    validation: Validation
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

    // Field error stats
    var emailError by rememberSaveable { mutableStateOf(false) }
    var usernameError by rememberSaveable { mutableStateOf(false) }
    var firstNameError by rememberSaveable { mutableStateOf(false) }
    var lastNameError by rememberSaveable { mutableStateOf(false) }
    var passwordError by rememberSaveable { mutableStateOf(false) }

    // Fields enable or disable state
    var enabled by rememberSaveable { mutableStateOf(true) }

    Column {
        // Text field for email
        OnboardingTextField(
            text = emailText,
            whenChanged = { newText ->
                emailText = newText
                if (emailError) {
                    emailError = false
                }
            },
            hint = "Email",
            leadingIcon = Icons.Filled.Email,
            trailingIcon = Icons.Filled.Clear,
            trailingIconOnClick = { emailText = "" },
            visualTransformation = VisualTransformation.None,
            error = emailError,
            errorText = "Email not valid",
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
            hint = "Username",
            leadingIcon = Icons.Filled.PersonOutline,
            trailingIcon = Icons.Filled.Clear,
            trailingIconOnClick = { usernameText = "" },
            visualTransformation = VisualTransformation.None,
            error = usernameError,
            errorText = "Username not valid",
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
            hint = "First Name",
            leadingIcon = Icons.Filled.Person,
            trailingIcon = Icons.Filled.Clear,
            trailingIconOnClick = { firstNameText = "" },
            visualTransformation = VisualTransformation.None,
            error = firstNameError,
            errorText = "Cannot be blank",
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
            hint = "Last Name",
            leadingIcon = Icons.Filled.Person,
            trailingIcon = Icons.Filled.Clear,
            trailingIconOnClick = { lastNameText = "" },
            visualTransformation = VisualTransformation.None,
            error = lastNameError,
            errorText = "Cannot be blank",
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
            hint = "Password",
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
                "Password must match"
            } else {
                "Cannot be blank"
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
            hint = "Confirm Password",
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
                "Password must match"
            } else {
                "Cannot be blank"
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
                    validation = validation
                )
                usernameError = verifyField(
                    text = usernameText,
                    type = Type.EMPTY,
                    validation = validation
                )
                firstNameError = verifyField(
                    text = firstNameText,
                    type = Type.EMPTY,
                    validation = validation
                )
                lastNameError = verifyField(
                    text = lastNameText,
                    type = Type.EMPTY,
                    validation = validation
                )
                passwordError = verifyField(
                    text = passwordText,
                    type = Type.PASSWORD,
                    extraText = passwordConfirmText,
                    validation = validation
                )
                if (!emailError && !usernameError && !firstNameError && !lastNameError && !passwordError) {
                    userViewModel.registerUser(
                        email = emailText,
                        password = passwordText,
                        firstName = firstNameText,
                        lastName = lastNameText,
                        username = usernameText
                    ) {
                        // Todo: Add error handling
                        enabled = true
                    }
                }
            },
            buttonText = "Register",
            enabled = enabled
        )
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
