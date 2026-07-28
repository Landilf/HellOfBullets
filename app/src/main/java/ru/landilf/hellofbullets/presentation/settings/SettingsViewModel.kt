package ru.landilf.hellofbullets.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.settings.ObserveGameSettingsUseCase
import ru.landilf.hellofbullets.domain.usecase.leaderboard.SyncSurvivalLeaderboardUseCase
import ru.landilf.hellofbullets.domain.usecase.settings.UpdateInputSensitivityUseCase
import ru.landilf.hellofbullets.domain.usecase.player.UpdatePlayerNameUseCase
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val observeGameSettingsUseCase: ObserveGameSettingsUseCase,
    private val updateInputSensitivityUseCase: UpdateInputSensitivityUseCase,
    private val updatePlayerNameUseCase: UpdatePlayerNameUseCase,
    private val syncSurvivalLeaderboardUseCase: SyncSurvivalLeaderboardUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    init {
        loadSettings()
    }

    fun onAction(
        action: SettingsAction
    ) {
        when (action) {
            is SettingsAction.OnPlayerNameChange -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        playerName = action.playerName,
                        errorMessage = null
                    )
                }
            }

            is SettingsAction.OnInputSensitivityChange -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        inputSensitivity = action.inputSensitivity,
                        errorMessage = null
                    )
                }
            }

            SettingsAction.OnInputSensitivityChangeFinished -> {
                saveInputSensitivity()
            }

            SettingsAction.OnSavePlayerNameClick -> {
                savePlayerName()
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val playerState = getOrCreatePlayerStateUseCase()

                _uiState.update { currentState ->
                    currentState.copy(
                        playerName = playerState.playerProfile.name
                    )
                }

                observeGameSettingsUseCase().collect { settings ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            inputSensitivity = settings.inputSensitivity
                        )
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Не удалось загрузить настройки"
                    )
                }
            }
        }
    }

    private fun saveInputSensitivity() {
        val inputSensitivity = _uiState.value.inputSensitivity

        viewModelScope.launch {
            try {
                updateInputSensitivityUseCase(
                    inputSensitivity = inputSensitivity
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = exception.message ?: "Не удалось сохранить чувствительность"
                    )
                }
            }
        }
    }

    private fun savePlayerName() {
        val playerName = _uiState.value.playerName

        viewModelScope.launch {
            try {
                updatePlayerNameUseCase(
                    name = playerName
                )
                _events.emit(SettingsEvent.PlayerNameSaved)
                syncLeaderboard()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = exception.message ?: "Не удалось сохранить имя"
                    )
                }
            }
        }
    }

    private fun syncLeaderboard() {
        viewModelScope.launch {
            try {
                syncSurvivalLeaderboardUseCase()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                // Локальное имя уже сохранено, синхронизация повторится позже
            }
        }
    }
}