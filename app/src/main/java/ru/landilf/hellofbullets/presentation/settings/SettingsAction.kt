package ru.landilf.hellofbullets.presentation.settings

sealed interface SettingsAction {
    data class OnPlayerNameChange(
        val playerName: String
    ) : SettingsAction

    data class OnInputSensitivityChange(
        val inputSensitivity: Float
    ) : SettingsAction

    data object OnInputSensitivityChangeFinished : SettingsAction

    data object OnSavePlayerNameClick : SettingsAction
}