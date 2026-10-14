package ru.landilf.hellofbullets.data.storage.mappers.equipment

import ru.landilf.hellofbullets.data.storage.entities.equipment.WeaponItemEntity
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import javax.inject.Inject

class WeaponItemEntityToDomainMapper @Inject constructor() :
        (WeaponItemEntity) -> WeaponItem {

    override fun invoke(entity: WeaponItemEntity): WeaponItem {
        return WeaponItem(
            id = entity.id,
            definitionId = entity.definitionId,
            level = entity.level,
            quality = EquipmentQuality.valueOf(entity.qualityName),
            additionalStatType = EquipmentStatType.valueOf(entity.additionalStatTypeName),
            additionalStatValue = entity.additionalStatValue,
            damage = entity.damage,
            attackSpeed = entity.attackSpeed,
            specializationCoef = entity.specializationCoef
        )
    }
}