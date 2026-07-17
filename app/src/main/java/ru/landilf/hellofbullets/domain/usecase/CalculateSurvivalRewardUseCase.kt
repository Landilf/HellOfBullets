package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.battle.common.result.RewardInfo
import javax.inject.Inject

class CalculateSurvivalRewardUseCase @Inject constructor() {
    operator fun invoke(time: Int, playerLevel: Int): RewardInfo {
        return RewardInfo(
            exp = kotlin.math.max(1, time / 10),
            silver = kotlin.math.max(1, time / 15)
        )
    }
}