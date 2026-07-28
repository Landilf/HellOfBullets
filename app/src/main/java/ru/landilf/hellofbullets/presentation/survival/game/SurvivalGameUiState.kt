package ru.landilf.hellofbullets.presentation.survival.game

import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalGameState
import ru.landilf.hellofbullets.domain.model.settings.GameSettings

data class SurvivalGameUiState(
    val isLoading: Boolean = true,
    val gameState: SurvivalGameState? = null,
    val errorMessage: String? = null,
    val isResultVisible: Boolean = false,
    val result: SurvivalResultUiState? = null,
    val inputSensitivity: Float = GameSettings.DEFAULT_INPUT_SENSITIVITY
)
