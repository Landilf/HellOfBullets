package ru.landilf.hellofbullets.domain.engine.equipment

import ru.landilf.hellofbullets.domain.generator.EquipmentRandomGenerator
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.repository.EquipmentQualityDistributionRepository
import javax.inject.Inject

class ShopEquipmentQualitySelector @Inject constructor(
    private val equipmentRandomGenerator: EquipmentRandomGenerator,
    private val equipmentQualityDistributionRepository: EquipmentQualityDistributionRepository
) {
    operator fun invoke(
        playerLevel: Int
    ): EquipmentQuality {
        val weights = equipmentQualityDistributionRepository.getWeightsForPlayerLevel(playerLevel)
        require(weights.isNotEmpty()) {
            "Распределение качеств снаряжения не может быть пустым"
        }

        val totalWeight = weights.sumOf { it.weight }
        val randomWeight = equipmentRandomGenerator.nextInt(totalWeight)

        var currentWeight = 0

        for (qualityWeight in weights) {
            currentWeight += qualityWeight.weight

            if (randomWeight < currentWeight) {
                return qualityWeight.quality
            }
        }

        error("Не удалось выбрать качество снаряжения")
    }
}