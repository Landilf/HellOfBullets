package ru.landilf.hellofbullets.data.storage.mappers.equipment

import ru.landilf.hellofbullets.data.storage.entities.equipment.ArtifactItemEntity
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import javax.inject.Inject

class ArtifactItemDomainToEntityMapper @Inject constructor() {
    operator fun invoke(
        item: ArtifactItem,
        ownerId: Long
    ): ArtifactItemEntity {
        return ArtifactItemEntity(
            id = item.id,
            ownerId = ownerId,
            definitionId = item.definitionId,
            level = item.level,
            qualityName = item.quality.name,
            additionalStatTypeName = item.additionalStatType.name,
            additionalStatValue = item.additionalStatValue,
            cooldownReductionPercent = item.cooldownReductionPercent,
            durationBonusPercent = item.durationBonusPercent
        )
    }
}