package ru.landilf.hellofbullets.data.storage.mappers

import ru.landilf.hellofbullets.data.storage.entities.player.PlayerProfileEntity
import ru.landilf.hellofbullets.domain.model.player.PlayerProfile

class PlayerProfileDomainToEntityMapper {
    operator fun invoke(domain: PlayerProfile): PlayerProfileEntity {
        return PlayerProfileEntity(
            id = domain.id,
            name = domain.name,
            level = domain.level,
            expAmount = domain.expAmount,
            silverAmount = domain.silverAmount
        )
    }
}