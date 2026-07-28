package ru.landilf.hellofbullets.presentation.settings

import ru.landilf.hellofbullets.domain.model.settings.GameSettings

data class SettingsUiState(
    val isLoading: Boolean = true,
    val playerName: String = "",
    val inputSensitivity: Float = GameSettings.DEFAULT_INPUT_SENSITIVITY,
    val errorMessage: String? = null
)
