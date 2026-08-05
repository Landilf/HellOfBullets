package ru.landilf.hellofbullets.data.storage.mappers.shop

import ru.landilf.hellofbullets.data.storage.entities.shop.ShopArtifactOfferEntity
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.shop.ShopOffer
import javax.inject.Inject

class ShopArtifactOfferDomainToEntityMapper @Inject constructor() {
    operator fun invoke(
        offer: ShopOffer,
        position: Int
    ): ShopArtifactOfferEntity {
        val item = requireNotNull(offer.item as? ArtifactItem)

        return ShopArtifactOfferEntity(
            itemId = item.id,
            position = position,
            definitionId = item.definitionId,
            level = item.level,
            qualityName = item.quality.name,
            additionalStatTypeName = item.additionalStatType.name,
            additionalStatValue = item.additionalStatValue,
            cooldownReductionPercent = item.cooldownReductionPercent,
            durationBonusPercent = item.durationBonusPercent,
            specializationCoef = item.specializationCoef,
            purchasePrice = offer.purchasePrice
        )
    }
}