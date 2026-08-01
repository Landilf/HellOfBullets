package ru.landilf.hellofbullets.data.storage.mappers.equipment

import ru.landilf.hellofbullets.data.storage.entities.equipment.WeaponItemEntity
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import javax.inject.Inject

class WeaponItemDomainToEntityMapper @Inject constructor() {
    operator fun invoke(
        item: WeaponItem,
        ownerId: Long
    ): WeaponItemEntity {
        return WeaponItemEntity(
            id = item.id,
            ownerId = ownerId,
            definitionId = item.definitionId,
            level = item.level,
            qualityName = item.quality.name,
            additionalStatTypeName = item.additionalStatType.name,
            additionalStatValue = item.additionalStatValue,
            damage = item.damage,
            attackSpeed = item.attackSpeed
        )
    }
}
