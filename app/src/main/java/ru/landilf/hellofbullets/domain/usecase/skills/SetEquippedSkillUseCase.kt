package ru.landilf.hellofbullets.domain.usecase.skills

import ru.landilf.hellofbullets.domain.model.player.PlayerStateUpdate
import ru.landilf.hellofbullets.domain.model.skills.SkillSlot
import ru.landilf.hellofbullets.domain.repository.PlayerRepository
import javax.inject.Inject

class SetEquippedSkillUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(
        slot: SkillSlot,
        skillId: Long?
    ) {
        playerRepository.updatePlayerState { playerState ->
            PlayerStateUpdate(
                updatedState = playerState.setEquippedSkill(
                    slot = slot,
                    skillId = skillId
                ),
                result = Unit
            )
        }
    }
}
