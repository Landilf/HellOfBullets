package ru.landilf.hellofbullets.domain.usecase.skills

import ru.landilf.hellofbullets.domain.model.player.PlayerStateUpdate
import ru.landilf.hellofbullets.domain.repository.PlayerRepository
import javax.inject.Inject

class UpgradePlayerSkillUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val getSkillDefinitionByIdUseCase: GetSkillDefinitionByIdUseCase
) {
    suspend operator fun invoke(
        skillId: Long
    ) {
        val definition = requireNotNull(
            getSkillDefinitionByIdUseCase(skillId)
        ) {
            "Не найден навык с id $skillId"
        }

        playerRepository.updatePlayerState { playerState ->
            val progress = requireNotNull(
                playerState.skillProgresses.firstOrNull { it.skillId == skillId }
            ) {
                "Навык с id $skillId не разблокирован"
            }

            val targetLevel = progress.level + 1
            require(targetLevel <= definition.maxLevel) {
                "Навык с id $skillId уже достиг максимального уровня"
            }

            val profile = playerState.playerProfile
            require(profile.skillPointAmount >= targetLevel) {
                "Недостаточно очков навыков для прокачки"
            }

            val updatedState = playerState.copy(
                playerProfile = profile.copy(
                    skillPointAmount = profile.skillPointAmount - targetLevel
                ),
                skillProgresses = playerState.skillProgresses.map { currentProgress ->
                    if (currentProgress.skillId == skillId) {
                        currentProgress.copy(level = targetLevel)
                    } else {
                        currentProgress
                    }
                }
            )

            PlayerStateUpdate(
                updatedState = updatedState,
                result = Unit
            )
        }
    }
}