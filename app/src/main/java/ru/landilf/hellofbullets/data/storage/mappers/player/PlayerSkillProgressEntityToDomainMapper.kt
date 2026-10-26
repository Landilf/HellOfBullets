package ru.landilf.hellofbullets.data.storage.mappers.player

import ru.landilf.hellofbullets.data.storage.entities.player.PlayerSkillProgressEntity
import ru.landilf.hellofbullets.domain.model.skills.SkillProgress
import javax.inject.Inject

class PlayerSkillProgressEntityToDomainMapper @Inject constructor() {
    operator fun invoke(
        entity: PlayerSkillProgressEntity
    ): SkillProgress {
        return SkillProgress(
            skillId = entity.skillId,
            level = entity.level
        )
    }
}