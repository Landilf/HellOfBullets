package ru.landilf.hellofbullets.domain.usecase.player

import ru.landilf.hellofbullets.domain.engine.player.PlayerProgressionCalculator
import ru.landilf.hellofbullets.domain.model.battle.common.result.RewardInfo
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import javax.inject.Inject

class ApplyPlayerRewardUseCase @Inject constructor(
    private val playerProgressionCalculator: PlayerProgressionCalculator,
    private val savePlayerStateUseCase: SavePlayerStateUseCase
) {
    suspend operator fun invoke(
        playerState: PlayerState,
        reward: RewardInfo
    ): PlayerState {
        require(reward.exp >= 0) {
            "Количество опыта в награде не может быть отрицательным"
        }
        require(reward.silver >= 0) {
            "Количество серебра в награде не может быть отрицательным"
        }

        val profile = playerState.playerProfile
        val totalExperience = profile.totalExperience + reward.exp
        val experienceProgress = playerProgressionCalculator.calculateProgress(totalExperience)
        val gainedLevels = experienceProgress.level - profile.level

        val updatedState = playerState.copy(
            playerProfile = profile.copy(
                level = experienceProgress.level,
                totalExperience = totalExperience,
                silverAmount = profile.silverAmount + reward.silver,
                skillPointAmount = profile.skillPointAmount + gainedLevels
            )
        )

        savePlayerStateUseCase(updatedState)

        return updatedState
    }
}