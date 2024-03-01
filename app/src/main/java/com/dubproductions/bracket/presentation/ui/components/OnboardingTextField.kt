package com.dubproductions.bracket.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

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
    enabled: Boolean,
    capitalize: Boolean
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
        enabled = enabled,
        keyboardOptions = if (capitalize) {
            KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            )
        } else {
            KeyboardOptions()
        }
    )
}
