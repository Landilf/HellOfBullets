package ru.landilf.hellofbullets.data.storage.mappers.shop

import ru.landilf.hellofbullets.data.storage.entities.shop.ShopArmorOfferEntity
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.shop.ShopOffer
import javax.inject.Inject

class ShopArmorOfferEntityToDomainMapper @Inject constructor() {
    operator fun invoke(
        entity: ShopArmorOfferEntity
    ): ShopOffer {
        return ShopOffer(
            item = ArmorItem(
                id = entity.itemId,
                definitionId = entity.definitionId,
                level = entity.level,
                quality = EquipmentQuality.valueOf(entity.qualityName),
                additionalStatType = EquipmentStatType.valueOf(entity.additionalStatTypeName),
                additionalStatValue = entity.additionalStatValue,
                hp = entity.hp,
                defense = entity.defense,
                specializationCoef = entity.specializationCoef
            ),
            purchasePrice = entity.purchasePrice
        )
    }
}