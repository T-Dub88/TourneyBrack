package com.dubproductions.bracket.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dubproductions.bracket.Type
import com.dubproductions.bracket.Validation
import com.dubproductions.bracket.navigation.Screen
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    navHostController: NavHostController,
    validation: Validation
) {

    // Text field texts
    var emailText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }

    // Password visibility
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Error stats
    var emailError by rememberSaveable { mutableStateOf(false) }
    var passwordError by rememberSaveable { mutableStateOf(false) }

    // Fields enable or disable state
    var enabled by rememberSaveable { mutableStateOf(true) }

    Column {
        Text(text = "Login")

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
            errorText = "Password needed",
            enabled = enabled
        )

        // Button to log user in
        OnboardingButton(
            whenClicked = {
                enabled = false
                emailError = validateFields(
                    text = emailText,
                    type = Type.EMAIL,
                    validation = validation
                )
                passwordError = validateFields(
                    text = passwordText,
                    type = Type.EMPTY,
                    validation = validation
                )
                // todo: move inside login function
                enabled = true
            },
            buttonText = "Login",
            enabled = enabled
        )

        // Button to navigate to the registration screen
        OnboardingButton(
            whenClicked = { navHostController.navigate(Screen.Registration.route) },
            buttonText = "Registration",
            enabled = enabled
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingTextField(
    text: String,
    whenChanged: (String) -> Unit,
    hint: String,
    leadingIcon: ImageVector,
    trailingIcon: ImageVector,
    trailingIconOnClick: () -> Unit,
    visualTransformation: VisualTransformation,
    error: Boolean,
    errorText: String,
    enabled: Boolean
) {
   OutlinedTextField(
       value = text,
       onValueChange = whenChanged,
       label = { Text(text = hint) },
       leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
       trailingIcon = {
           if (error) {
               Icon(imageVector = Icons.Filled.Error, contentDescription = null)
           } else {
               IconButton(onClick = trailingIconOnClick) {
                   Icon(imageVector = trailingIcon, contentDescription = null)
               }
           }
       },
       modifier = Modifier
           .fillMaxWidth()
           .padding(
               horizontal = 25.dp
           ),
       visualTransformation = visualTransformation,
       isError = error,
       supportingText = {
           if (error) {
               Text(text = errorText)
           }
       },
       enabled = enabled
   )
}

@Composable
fun OnboardingButton(
    whenClicked: () -> Unit,
    buttonText: String,
    enabled: Boolean
) {
    Button(
        onClick = whenClicked,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp),
        enabled = enabled
    ) {
        Text(text = buttonText)
    }
}
