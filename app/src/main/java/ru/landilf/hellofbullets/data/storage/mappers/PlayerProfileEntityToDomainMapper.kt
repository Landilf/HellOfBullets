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
            expAmount = entity.expAmount,
            silverAmount = entity.silverAmount
        )
    }
}