package com.dubproductions.bracket.core.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.dubproductions.bracket.R

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
        }
    )
}
