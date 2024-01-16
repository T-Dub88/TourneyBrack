package com.dubproductions.bracket.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dubproductions.bracket.R
import com.dubproductions.bracket.data.Tournament
import java.text.SimpleDateFormat
import java.util.Locale

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
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentSummaryCard(
    tournament: Tournament,
    onPress: () -> Unit,
    tapCardInstruction: String
) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        onClick = onPress
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(
                    text = stringResource(
                        id = R.string.tournament_name,
                        tournament.name.toString()
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.rounds,
                        tournament.setNumberOfRounds().toString()
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.status,
                        tournament.status.toString()
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        id = R.string.type,
                        tournament.type.toString()
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.started_date,
                        if (tournament.timeStarted != null) {
                            formatDateTime(tournament.timeStarted)
                        } else {
                            stringResource(id = R.string.pending)
                        }

                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.completed_date,
                        if (tournament.timeCompleted != null) {
                            formatDateTime(tournament.timeCompleted)
                        } else {
                            stringResource(R.string.pending)
                        }
                    ),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )
            }
        }
        Text(
            text = tapCardInstruction,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}

fun formatDateTime(timestamp: Long?): String {
    val sdf = SimpleDateFormat("MM-dd-yy HH:mm", Locale.getDefault())
    return sdf.format(timestamp)
}
