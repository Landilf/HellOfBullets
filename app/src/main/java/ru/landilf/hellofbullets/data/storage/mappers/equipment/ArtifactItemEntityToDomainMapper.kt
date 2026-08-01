package ru.landilf.hellofbullets.data.storage.mappers.equipment

import ru.landilf.hellofbullets.data.storage.entities.equipment.ArtifactItemEntity
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import javax.inject.Inject

class ArtifactItemEntityToDomainMapper @Inject constructor() :
        (ArtifactItemEntity) -> ArtifactItem {

    override fun invoke(entity: ArtifactItemEntity): ArtifactItem {
        return ArtifactItem(
            id = entity.id,
            definitionId = entity.definitionId,
            level = entity.level,
            quality = EquipmentQuality.valueOf(entity.qualityName),
            additionalStatType = EquipmentStatType.valueOf(entity.additionalStatTypeName),
            additionalStatValue = entity.additionalStatValue,
            cooldownReductionPercent = entity.cooldownReductionPercent,
            durationBonusPercent = entity.durationBonusPercent
        )
    }
}