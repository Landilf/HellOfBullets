package ru.landilf.hellofbullets.data.storage.mappers.shop

import ru.landilf.hellofbullets.data.storage.entities.shop.ShopWeaponOfferEntity
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.shop.ShopOffer
import javax.inject.Inject

class ShopWeaponOfferEntityToDomainMapper @Inject constructor() {
    operator fun invoke(
        entity: ShopWeaponOfferEntity
    ): ShopOffer {
        return ShopOffer(
            item = WeaponItem(
                id = entity.itemId,
                definitionId = entity.definitionId,
                level = entity.level,
                quality = EquipmentQuality.valueOf(entity.qualityName),
                additionalStatType = EquipmentStatType.valueOf(entity.additionalStatTypeName),
                additionalStatValue = entity.additionalStatValue,
                damage = entity.damage,
                attackSpeed = entity.attackSpeed,
                specializationCoef = entity.specializationCoef
            ),
            purchasePrice = entity.purchasePrice,
            isSold = entity.isSold
        )
    }
}