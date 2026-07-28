package ru.landilf.hellofbullets.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.landilf.hellofbullets.domain.model.settings.GameSettings

interface SettingsRepository {
    fun observeSettings(): Flow<GameSettings>

    suspend fun updateInputSensitivity(
        inputSensitivity: Float
    )
}