package ru.landilf.hellofbullets.domain.usecase.skills

import ru.landilf.hellofbullets.domain.model.player.PlayerStateUpdate
import ru.landilf.hellofbullets.domain.model.skills.SkillProgress
import ru.landilf.hellofbullets.domain.repository.PlayerRepository
import javax.inject.Inject

class UnlockPlayerSkillUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val getSkillDefinitionByIdUseCase: GetSkillDefinitionByIdUseCase
) {
    suspend operator fun invoke(skillId: Long) {
        val definition = requireNotNull(
            getSkillDefinitionByIdUseCase(skillId)
        ) {
            "Не найден навык с id $skillId"
        }

        playerRepository.updatePlayerState { playerState ->
            require(
                playerState.skillProgresses.none { progress ->
                    progress.skillId == skillId
                }
            ) {
                "Навык с id $skillId уже разблокирован"
            }

            val profile = playerState.playerProfile
            require(profile.skillPointAmount >= definition.unlockCost) {
                "Недостаточно очков навыков для разблокировки"
            }

            val updatedState = playerState.copy(
                playerProfile = profile.copy(
                    skillPointAmount = profile.skillPointAmount - definition.unlockCost
                ),
                skillProgresses = playerState.skillProgresses + SkillProgress(
                    skillId = skillId,
                    level = 1
                )
            )

            PlayerStateUpdate(
                updatedState = updatedState,
                result = Unit
            )
        }
    }
}