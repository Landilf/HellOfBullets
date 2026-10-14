package ru.landilf.hellofbullets.data.storage.mappers.player

import ru.landilf.hellofbullets.data.storage.entities.player.PlayerBuildEntity
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.player.PlayerBuild
import javax.inject.Inject

class PlayerBuildEntityToDomainMapper @Inject constructor() {
    operator fun invoke(
        entity: PlayerBuildEntity?,
        items: List<Item>
    ): PlayerBuild {
        return PlayerBuild(
            equippedWeaponItem = items
                .filterIsInstance<WeaponItem>()
                .firstOrNull { it.id == entity?.equippedWeaponItemId },
            equippedArmorItem = items
                .filterIsInstance<ArmorItem>()
                .firstOrNull { it.id == entity?.equippedArmorItemId },
            equippedArtifactItem = items
                .filterIsInstance<ArtifactItem>()
                .firstOrNull { it.id == entity?.equippedArtifactItemId },
            firstSkillSlot = null,
            secondSkillSlot = null
        )
    }
}