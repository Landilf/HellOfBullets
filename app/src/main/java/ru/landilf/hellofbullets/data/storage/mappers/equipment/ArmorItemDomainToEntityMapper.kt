package ru.landilf.hellofbullets.data.storage.mappers.equipment

import ru.landilf.hellofbullets.data.storage.entities.equipment.ArmorItemEntity
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import javax.inject.Inject

class ArmorItemDomainToEntityMapper @Inject constructor() {
    operator fun invoke(
        item: ArmorItem,
        ownerId: Long
    ): ArmorItemEntity {
        return ArmorItemEntity(
            id = item.id,
            ownerId = ownerId,
            definitionId = item.definitionId,
            level = item.level,
            qualityName = item.quality.name,
            additionalStatTypeName = item.additionalStatType.name,
            additionalStatValue = item.additionalStatValue,
            hp = item.hp,
            defense = item.defense,
            specializationCoef = item.specializationCoef
        )
    }
}