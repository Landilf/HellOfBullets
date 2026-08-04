package ru.landilf.hellofbullets.domain.engine.equipment

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import javax.inject.Inject

class EquipmentPurchasePriceCalculator @Inject constructor() {
    operator fun invoke(
        basePurchasePrice: Int,
        quality: EquipmentQuality
    ): Int {
        require(basePurchasePrice > 0) {
            "Базовая цена предмета должна быть положительной"
        }

        return basePurchasePrice * quality.purchasePriceMultiplier
    }
}