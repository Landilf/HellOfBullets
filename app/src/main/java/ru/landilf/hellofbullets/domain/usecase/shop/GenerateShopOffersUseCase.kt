package ru.landilf.hellofbullets.domain.usecase.shop

import ru.landilf.hellofbullets.domain.engine.equipment.EquipmentItemFactory
import ru.landilf.hellofbullets.domain.engine.equipment.EquipmentPurchasePriceCalculator
import ru.landilf.hellofbullets.domain.engine.equipment.ShopEquipmentQualitySelector
import ru.landilf.hellofbullets.domain.generator.EquipmentRandomGenerator
import ru.landilf.hellofbullets.domain.model.shop.ShopOffer
import ru.landilf.hellofbullets.domain.repository.EquipmentDefinitionRepository
import javax.inject.Inject

class GenerateShopOffersUseCase @Inject constructor(
    private val equipmentDefinitionRepository: EquipmentDefinitionRepository,
    private val equipmentRandomGenerator: EquipmentRandomGenerator,
    private val shopEquipmentQualitySelector: ShopEquipmentQualitySelector,
    private val equipmentItemFactory: EquipmentItemFactory,
    private val equipmentPurchasePriceCalculator: EquipmentPurchasePriceCalculator
) {
    suspend operator fun invoke(
        playerLevel: Int
    ): List<ShopOffer> {
        require(playerLevel > 0) {
            "Уровень игрока должен быть положительным"
        }

        val definitions = equipmentDefinitionRepository.getDefinitions()
        require(definitions.isNotEmpty()) {
            "Каталог определений снаряжения не может быть пустым"
        }

        return List(SHOP_OFFERS_COUNT) {
            val definition = definitions[equipmentRandomGenerator.nextInt(definitions.size)]
            val quality = shopEquipmentQualitySelector(playerLevel)
            val item = equipmentItemFactory(
                definition = definition,
                quality = quality
            )

            ShopOffer(
                item = item,
                purchasePrice = equipmentPurchasePriceCalculator(
                    basePurchasePrice = definition.basePurchasePrice,
                    quality = quality
                )
            )
        }
    }

    private companion object {
        const val SHOP_OFFERS_COUNT = 16
    }

}