package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.battle.RewardInfo

class CalculateSurvivalRewardUseCase {
    operator fun invoke(time: Int, playerLevel: Int): RewardInfo {
        return RewardInfo(
            exp = 0,
            silver = 0
        )
    }
}