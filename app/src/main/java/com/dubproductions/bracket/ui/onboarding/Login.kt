package com.dubproductions.bracket.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dubproductions.bracket.viewmodel.UserViewModel

@Composable
fun LoginScreen(userViewModel: UserViewModel, navHostController: NavHostController) {

    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    Column(

    ) {
        Text(text = "Login")
        LoginScreenTextField(
            text = emailText,
            whenChanged = { newText -> emailText = newText },
            hint = "Email",
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 25.dp
                )
        )
        LoginScreenTextField(
            text = passwordText,
            whenChanged = { newText -> passwordText = newText },
            hint = "Password",
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 25.dp
                )
        )
        LoginScreenButton(
            whenClicked = { /*TODO*/ },
            buttonText = "Login"
        )
        LoginScreenButton(
            whenClicked = { /*TODO*/ },
            buttonText = "Registration"
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenTextField(
    text: String,
    whenChanged: (String) -> Unit,
    hint: String,
    modifier: Modifier
) {
   OutlinedTextField(
       value = text,
       onValueChange = whenChanged,
       label = { Text(text = hint) },
       modifier = modifier
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
