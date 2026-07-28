package ru.landilf.hellofbullets.presentation.settings

sealed interface SettingsEvent {
    data object PlayerNameSaved : SettingsEvent
}