package com.dubproductions.bracket.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Person3
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material.icons.filled.VerifiedUser
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
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun RegistrationScreen(userViewModel: UserViewModel, navHostController: NavHostController) {

    var emailText by rememberSaveable { mutableStateOf("") }
    var usernameText by rememberSaveable { mutableStateOf("") }
    var firstNameText by rememberSaveable { mutableStateOf("") }
    var lastNameText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }
    var passwordConfirmText by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column {
        // Text field for email
        OnboardingTextField(
            text = emailText,
            whenChanged = { newText -> emailText = newText },
            hint = "Email",
            leadingIcon = Icons.Filled.Email,
            trailingIcon = Icons.Filled.Clear,
            trailingIconOnClick = { emailText = "" },
            visualTransformation = VisualTransformation.None
        )

        // Text field for username
        OnboardingTextField(
            text = usernameText,
            whenChanged = { newText -> usernameText = newText},
            hint = "Username",
            leadingIcon = Icons.Filled.PersonOutline,
            trailingIcon = Icons.Filled.Clear,
            trailingIconOnClick = { usernameText = "" },
            visualTransformation = VisualTransformation.None
        )

        // Text field for first name
        OnboardingTextField(
            text = firstNameText,
            whenChanged = { newText -> firstNameText = newText},
            hint = "First Name",
            leadingIcon = Icons.Filled.Person,
            trailingIcon = Icons.Filled.Clear,
            trailingIconOnClick = { firstNameText = "" },
            visualTransformation = VisualTransformation.None
        )

        // Text field for last name
        OnboardingTextField(
            text = lastNameText,
            whenChanged = { newText -> lastNameText = newText},
            hint = "Last Name",
            leadingIcon = Icons.Filled.Person,
            trailingIcon = Icons.Filled.Clear,
            trailingIconOnClick = { lastNameText = "" },
            visualTransformation = VisualTransformation.None
        )

        // Text field for password
        OnboardingTextField(
            text = passwordText,
            whenChanged = { newText -> passwordText = newText},
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
            }
        )

        // Text field for confirm password
        OnboardingTextField(
            text = passwordConfirmText,
            whenChanged = { newText -> passwordConfirmText = newText},
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
            }
        )
    }
}
