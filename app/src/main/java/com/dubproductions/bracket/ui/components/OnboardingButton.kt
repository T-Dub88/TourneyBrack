package com.dubproductions.bracket.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
