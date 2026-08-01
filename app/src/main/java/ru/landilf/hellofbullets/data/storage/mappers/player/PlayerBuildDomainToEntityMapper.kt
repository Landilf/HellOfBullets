package ru.landilf.hellofbullets.data.storage.mappers.player

import ru.landilf.hellofbullets.data.storage.entities.player.PlayerBuildEntity
import ru.landilf.hellofbullets.domain.model.player.PlayerBuild
import javax.inject.Inject

class PlayerBuildDomainToEntityMapper @Inject constructor() {
    operator fun invoke(
        build: PlayerBuild,
        playerId: Long
    ): PlayerBuildEntity {
        return PlayerBuildEntity(
            playerId = playerId,
            equippedWeaponItemId = build.equippedWeaponItem?.id,
            equippedArmorItemId = build.equippedArmorItem?.id,
            equippedArtifactItemId = build.equippedArtifactItem?.id
        )
    }
}