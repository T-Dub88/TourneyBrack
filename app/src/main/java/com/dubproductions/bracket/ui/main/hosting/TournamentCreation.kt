package com.dubproductions.bracket.ui.main.hosting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dubproductions.bracket.R
import com.dubproductions.bracket.Type
import com.dubproductions.bracket.Validation
import com.dubproductions.bracket.ui.components.ReusableDialog
import com.dubproductions.bracket.data.ui_state.TournamentCreationUIState

@Composable
fun TournamentCreationScreen(
    uiState: TournamentCreationUIState,
    createTournament: (name: String, participants: String, type: String) -> Unit,
    dismissDialog: (Boolean) -> Unit
) {

    val dropDownItems = listOf(
        stringResource(id = R.string.selected_tournament_type),
        stringResource(id = R.string.swiss)
    )

    var tournamentName by rememberSaveable { mutableStateOf("") }
    var tournamentParticipants by rememberSaveable { mutableStateOf("") }
    var dropDownExpanded by rememberSaveable { mutableStateOf(false) }
    var dropDownSelectedItem by rememberSaveable { mutableStateOf(dropDownItems[0]) }

    var nameError by rememberSaveable {
        mutableStateOf(false)
    }
    var typeError by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .padding(top = 8.dp)
    ) {
        // Tournament name field
        TournamentCreationTextField(
            text = tournamentName,
            onValueChanged = {
                if (nameError) {
                    nameError = false
                }
                tournamentName = it
            },
            hint = stringResource(id = R.string.tournament_name_hint),
            trailingIconOnClick = { tournamentName = "" },
            enabled = uiState.screenEnabled,
            error = nameError
        )

        // Selection for tournament type
        ExposedDropDownMenu(
            items = dropDownItems,
            selectedItem = dropDownSelectedItem,
            onItemSelected = {
                if (typeError) {
                    typeError = false
                }
                dropDownSelectedItem = it
            },
            expandOnClick = { dropDownExpanded = it },
            expanded = dropDownExpanded,
            enabled = uiState.screenEnabled,
            error = typeError
        )

        // Participants field
        TournamentCreationTextField(
            text = tournamentParticipants,
            onValueChanged = { tournamentParticipants = it },
            hint = stringResource(id = R.string.participants_hint),
            trailingIconOnClick = { tournamentParticipants = "" },
            enabled = uiState.screenEnabled,
            error = false
        )

        Text(
            text = stringResource(id = R.string.participants_helper_text),
            modifier = Modifier
                .padding(start = 16.dp)
        )

        CreateTournamentButton(
            enabled = uiState.screenEnabled,
            onButtonClick = {
                nameError = validateFields(
                    text = tournamentName,
                    type = Type.EMPTY
                )

                typeError = validateFields(
                    text = dropDownSelectedItem,
                    type = Type.TOURNEY_TYPE
                )

                if (!nameError && !typeError) {
                    createTournament(
                        tournamentName,
                        tournamentParticipants,
                        dropDownSelectedItem
                    )
                }
            }
        )

        when(uiState.successfulCreation) {
            true -> {
                ReusableDialog(
                    titleText = stringResource(id = R.string.creation_success_header),
                    contentText = stringResource(id = R.string.creation_success_text),
                    icon = Icons.Outlined.CheckCircle,
                    dismissDialog = {
                        dismissDialog(true)
                    }
                )
            }
            false -> {
                ReusableDialog(
                    titleText = stringResource(id = R.string.creation_failure_header),
                    contentText = stringResource(id = R.string.creation_failure_text),
                    icon = Icons.Outlined.Error,
                    dismissDialog = {
                        dismissDialog(false)
                    }
                )
            }
            null -> {}
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TournamentCreationTextField(
    text: String,
    onValueChanged: (String) -> Unit,
    hint: String,
    trailingIconOnClick: () -> Unit,
    enabled: Boolean,
    error: Boolean
) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        trailingIcon = {
            IconButton(
                onClick =  trailingIconOnClick,
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = null
                )
            }
        },
        label = { Text(text = hint) },
        enabled = enabled,
        isError = error,
        supportingText = {
            if (error) {
                Text(text = stringResource(id = R.string.cannot_be_blank))
            }
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropDownMenu(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    expandOnClick: (Boolean) -> Unit,
    expanded: Boolean,
    enabled: Boolean,
    error: Boolean
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) {
                expandOnClick(!expanded)
            }
        },
        modifier = Modifier
            .padding(top = 8.dp)
            .padding(horizontal = 8.dp)
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedItem,
            onValueChange = { },
            trailingIcon = {
                ExposedDropdownMenuDefaults
                    .TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            enabled = enabled,
            isError = error,
            supportingText = {
                if (error) {
                    Text(text = stringResource(id = R.string.must_select_type))
                }
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expandOnClick(false) }
        ) {
            items.forEach { selectedItem ->
                DropdownMenuItem(
                    text = { Text(text = selectedItem) },
                    onClick = {
                        onItemSelected(selectedItem)
                        expandOnClick(false)
                    },
                    enabled = enabled
                )
            }
        }
    }
}

@Composable
private fun CreateTournamentButton(
    onButtonClick: () -> Unit,
    enabled: Boolean
) {
    Button(
        onClick = onButtonClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        enabled = enabled
    ) {
        Text(text = stringResource(id = R.string.create_tournament))
    }
}

private fun validateFields(
    text: String,
    type: Type
): Boolean {
    return !when(type) {
        Type.EMPTY -> {
            Validation.isFieldEmpty(text)
        }
        Type.TOURNEY_TYPE -> {
            Validation.selectedTourneyType(text)
        }
        else -> false
    }
}
