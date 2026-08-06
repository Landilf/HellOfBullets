package ru.landilf.hellofbullets.data.storage.mappers.shop

import ru.landilf.hellofbullets.data.storage.entities.shop.ShopArmorOfferEntity
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.shop.ShopOffer
import javax.inject.Inject

class ShopArmorOfferDomainToEntityMapper @Inject constructor() {
    operator fun invoke(
        offer: ShopOffer,
        position: Int
    ): ShopArmorOfferEntity {
        val item = requireNotNull(offer.item as? ArmorItem)

        return ShopArmorOfferEntity(
            itemId = item.id,
            position = position,
            definitionId = item.definitionId,
            level = item.level,
            qualityName = item.quality.name,
            additionalStatTypeName = item.additionalStatType.name,
            additionalStatValue = item.additionalStatValue,
            hp = item.hp,
            defense = item.defense,
            specializationCoef = item.specializationCoef,
            purchasePrice = offer.purchasePrice,
            isSold = offer.isSold
        )
    }
}