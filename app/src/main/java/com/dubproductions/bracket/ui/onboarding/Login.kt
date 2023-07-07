package com.dubproductions.bracket.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
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
import com.dubproductions.bracket.R
import com.dubproductions.bracket.navigation.Screen
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun LoginScreen(userViewModel: UserViewModel, navHostController: NavHostController) {

    var emailText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column {
        Text(text = "Login")

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

        // Text field for password
        OnboardingTextField(
            text = passwordText,
            whenChanged = { newText -> passwordText = newText },
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

        // Button to log user in
        LoginScreenButton(
            whenClicked = { /*TODO*/ },
            buttonText = "Login"
        )

        // Button to navigate to the registration screen
        LoginScreenButton(
            whenClicked = { navHostController.navigate(Screen.Registration.route) },
            buttonText = "Registration"
        )
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
    visualTransformation: VisualTransformation
) {
   OutlinedTextField(
       value = text,
       onValueChange = whenChanged,
       label = { Text(text = hint) },
       leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
       trailingIcon = {
           IconButton(onClick = trailingIconOnClick) {
               Icon(imageVector = trailingIcon, contentDescription = null)
           }
       },
       modifier = Modifier
           .fillMaxWidth()
           .padding(
               horizontal = 25.dp
           ),
       visualTransformation = visualTransformation
   )
}

@Composable
fun LoginScreenButton(
    whenClicked: () -> Unit,
    buttonText: String
) {
    Button(
        onClick = whenClicked,
    ) {
        Text(text = buttonText)
    }
}
