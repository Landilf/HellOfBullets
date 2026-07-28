package ru.landilf.hellofbullets.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.domain.model.settings.GameSettings
import ru.landilf.hellofbullets.presentation.common.CenteredMessage

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    events: Flow<SettingsEvent>,
    onAction: (SettingsAction) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val playerNameSavedMessage = stringResource(R.string.settings_name_saved)

    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                SettingsEvent.PlayerNameSaved -> {
                    snackbarHostState.showSnackbar(
                        message = playerNameSavedMessage
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.isLoading) {
            CenteredMessage(
                message = stringResource(R.string.loading_title)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.main_menu_settings),
                    style = MaterialTheme.typography.headlineMedium
                )

                OutlinedTextField(
                    value = state.playerName,
                    onValueChange = { playerName ->
                        onAction(
                            SettingsAction.OnPlayerNameChange(
                                playerName = playerName
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = {
                        Text(stringResource(R.string.settings_player_name_label))
                    },
                    singleLine = true
                )

                Button(
                    onClick = { onAction(SettingsAction.OnSavePlayerNameClick) },
                    enabled = state.playerName.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.settings_save_name))
                }

                Text(stringResource(R.string.settings_input_sensitivity))

                Text(
                    text = stringResource(
                        R.string.settings_input_sensitivity_value,
                        state.inputSensitivity
                    ),
                    style = MaterialTheme.typography.titleMedium
                )

                Slider(
                    value = state.inputSensitivity,
                    onValueChange = { inputSensitivity ->
                        onAction(
                            SettingsAction.OnInputSensitivityChange(inputSensitivity)
                        )
                    },
                    onValueChangeFinished = { onAction(SettingsAction.OnInputSensitivityChangeFinished) },
                    valueRange = GameSettings.MIN_INPUT_SENSITIVITY..GameSettings.MAX_INPUT_SENSITIVITY,
                    steps = 24,
                    modifier = Modifier.fillMaxWidth()
                )

                state.errorMessage?.let { errorMessage ->
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}