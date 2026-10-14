package ru.landilf.hellofbullets.data.storage.mappers.equipment

import ru.landilf.hellofbullets.data.storage.entities.equipment.ArmorItemEntity
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import javax.inject.Inject

class ArmorItemEntityToDomainMapper @Inject constructor() :
        (ArmorItemEntity) -> ArmorItem {

    override fun invoke(entity: ArmorItemEntity): ArmorItem {
        return ArmorItem(
            id = entity.id,
            definitionId = entity.definitionId,
            level = entity.level,
            quality = EquipmentQuality.valueOf(entity.qualityName),
            additionalStatType = EquipmentStatType.valueOf(entity.additionalStatTypeName),
            additionalStatValue = entity.additionalStatValue,
            hp = entity.hp,
            defense = entity.defense,
            specializationCoef = entity.specializationCoef
        )
    }
}