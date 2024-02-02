package com.dubproductions.bracket.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dubproductions.bracket.R

@Composable
fun EditMatchDialog(
    hideDialog: () -> Unit,
    editMatch: () -> Unit
) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.edit_match)) },
        text = { Text(text = stringResource(id = R.string.edit_match_body)) },
        icon = { Icons.Default.Warning },
        onDismissRequest = hideDialog,
        confirmButton = {
            TextButton(onClick = editMatch) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = hideDialog) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}
