package ru.landilf.hellofbullets.data.storage.mappers.player

import ru.landilf.hellofbullets.data.storage.entities.player.PlayerSkillProgressEntity
import ru.landilf.hellofbullets.domain.model.skills.SkillProgress
import javax.inject.Inject

class PlayerSkillProgressDomainToEntityMapper @Inject constructor() {
    operator fun invoke(
        progress: SkillProgress,
        playerId: Long
    ): PlayerSkillProgressEntity {
        return PlayerSkillProgressEntity(
            playerId = playerId,
            skillId = progress.skillId,
            level = progress.level
        )
    }
}