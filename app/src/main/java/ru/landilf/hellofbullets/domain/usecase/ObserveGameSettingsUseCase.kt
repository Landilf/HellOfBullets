package ru.landilf.hellofbullets.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.landilf.hellofbullets.domain.model.settings.GameSettings
import ru.landilf.hellofbullets.domain.repository.SettingsRepository
import javax.inject.Inject

class ObserveGameSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<GameSettings> {
        return settingsRepository.observeSettings()
    }
}