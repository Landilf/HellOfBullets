package ru.landilf.hellofbullets.presentation.survival.game

import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalGameState

data class SurvivalGameUiState(
    val isLoading: Boolean = true,
    val gameState: SurvivalGameState? = null,
    val errorMessage: String? = null,
    val isPaused: Boolean = false,
    val isResultVisible: Boolean = false
)
