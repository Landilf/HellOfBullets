package ru.landilf.hellofbullets.data.storage.mappers.shop

import ru.landilf.hellofbullets.data.storage.entities.shop.ShopArtifactOfferEntity
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.shop.ShopOffer
import javax.inject.Inject

class ShopArtifactOfferEntityToDomainMapper @Inject constructor() {
    operator fun invoke(
        entity: ShopArtifactOfferEntity
    ): ShopOffer {
        return ShopOffer(
            item = ArtifactItem(
                id = entity.itemId,
                definitionId = entity.definitionId,
                level = entity.level,
                quality = EquipmentQuality.valueOf(entity.qualityName),
                additionalStatType = EquipmentStatType.valueOf(entity.additionalStatTypeName),
                additionalStatValue = entity.additionalStatValue,
                cooldownReductionPercent = entity.cooldownReductionPercent,
                durationBonusPercent = entity.durationBonusPercent,
                specializationCoef = entity.specializationCoef
            ),
            purchasePrice = entity.purchasePrice,
            isSold = entity.isSold
        )
    }
}