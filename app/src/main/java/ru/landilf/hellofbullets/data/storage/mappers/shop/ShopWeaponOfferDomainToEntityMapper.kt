package ru.landilf.hellofbullets.data.storage.mappers.shop

import ru.landilf.hellofbullets.data.storage.entities.shop.ShopWeaponOfferEntity
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.shop.ShopOffer
import javax.inject.Inject

class ShopWeaponOfferDomainToEntityMapper @Inject constructor() {
    operator fun invoke(
        offer: ShopOffer,
        position: Int
    ): ShopWeaponOfferEntity {
        val item = requireNotNull(offer.item as? WeaponItem)

        return ShopWeaponOfferEntity(
            itemId = item.id,
            position = position,
            definitionId = item.definitionId,
            level = item.level,
            qualityName = item.quality.name,
            additionalStatTypeName = item.additionalStatType.name,
            additionalStatValue = item.additionalStatValue,
            damage = item.damage,
            attackSpeed = item.attackSpeed,
            specializationCoef = item.specializationCoef,
            purchasePrice = offer.purchasePrice,
            isSold = offer.isSold
        )
    }
}