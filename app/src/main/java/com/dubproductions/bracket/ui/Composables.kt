package com.dubproductions.bracket.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.dubproductions.bracket.R

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

@Composable
fun ReusableDialog(
    titleText: String,
    contentText: String,
    icon: ImageVector,
    dismissDialog: () -> Unit
) {
    AlertDialog(
        title = { Text(text = titleText) },
        text = { Text(text = contentText) },
        icon = { Icon(imageVector = icon, contentDescription = null) },
        onDismissRequest = { dismissDialog() },
        confirmButton = {
            TextButton(
                onClick = { dismissDialog() }
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
    )
}
