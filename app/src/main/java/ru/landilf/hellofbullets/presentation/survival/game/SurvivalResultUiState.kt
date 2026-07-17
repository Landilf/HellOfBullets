package ru.landilf.hellofbullets.presentation.survival.game

import ru.landilf.hellofbullets.domain.model.battle.common.result.RewardInfo

data class SurvivalResultUiState(
    val elapsedTimeMs: Int,
    val reward: RewardInfo
)
