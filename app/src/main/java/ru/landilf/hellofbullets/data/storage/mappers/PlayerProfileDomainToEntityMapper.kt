package ru.landilf.hellofbullets.data.storage.mappers

import ru.landilf.hellofbullets.data.storage.entities.player.PlayerProfileEntity
import ru.landilf.hellofbullets.domain.model.player.PlayerProfile
import javax.inject.Inject

class PlayerProfileDomainToEntityMapper @Inject constructor() {
    operator fun invoke(domain: PlayerProfile): PlayerProfileEntity {
        return PlayerProfileEntity(
            id = domain.id,
            name = domain.name,
            level = domain.level,
            totalExperience = domain.totalExperience,
            silverAmount = domain.silverAmount,
            skillPointAmount = domain.skillPointAmount
        )
    }
}