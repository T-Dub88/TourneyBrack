package com.dubproductions.bracket.hosting.presentation.edit.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BracketGenerationDialog(
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    title: String,
    body: String,
    positiveButton: String,
    dismissButton: String,
    icon: ImageVector
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = body) },
        icon = { Icon(imageVector = icon, contentDescription = null) },
        confirmButton = {
            TextButton(
                onClick = onConfirmClick
            ) {
                Text(text = positiveButton)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancelClick
            ) {
                Text(text = dismissButton)
            }
        },
        onDismissRequest = onCancelClick
    )
}
