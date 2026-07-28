package ru.landilf.hellofbullets.domain.usecase.settings

import ru.landilf.hellofbullets.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateInputSensitivityUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(
        inputSensitivity: Float
    ) {
        settingsRepository.updateInputSensitivity(inputSensitivity)
    }
}