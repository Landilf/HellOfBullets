package ru.landilf.hellofbullets.data.storage.mappers

import ru.landilf.hellofbullets.data.storage.entities.player.PlayerProfileEntity
import ru.landilf.hellofbullets.domain.model.player.PlayerProfile
import javax.inject.Inject

class PlayerProfileEntityToDomainMapper @Inject constructor() {
    operator fun invoke(entity: PlayerProfileEntity): PlayerProfile {
        return PlayerProfile(
            id = entity.id,
            name = entity.name,
            level = entity.level,
            totalExperience = entity.totalExperience,
            silverAmount = entity.silverAmount,
            skillPointAmount = entity.skillPointAmount
        )
    }
}