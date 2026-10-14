package ru.landilf.hellofbullets.data.catalog.equipment

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQualityWeight
import ru.landilf.hellofbullets.domain.repository.EquipmentQualityDistributionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaticEquipmentQualityDistributionRepository @Inject constructor() :
    EquipmentQualityDistributionRepository {

    override fun getWeightsForPlayerLevel(
        playerLevel: Int
    ): List<EquipmentQualityWeight> {
        require(playerLevel > 0) {
            "Уровень игрока должен быть положительным"
        }

        return distributions.first { playerLevel in it.playerLevelRange }.weights
    }

    private data class QualityDistribution(
        val playerLevelRange: IntRange,
        val weights: List<EquipmentQualityWeight>
    ) {
        init {
            require(weights.sumOf { it.weight } == 100) {
                "Сумма весов качества должна быть равна 100"
            }
        }
    }

    private companion object {
        val distributions = listOf(
            QualityDistribution(
                playerLevelRange = 1..5,
                weights = listOf(
                    EquipmentQualityWeight(EquipmentQuality.NORMAL, 70),
                    EquipmentQualityWeight(EquipmentQuality.FINE, 25),
                    EquipmentQualityWeight(EquipmentQuality.SUPERIOR, 5),
                )
            ),
            QualityDistribution(
                playerLevelRange = 6..12,
                weights = listOf(
                    EquipmentQualityWeight(EquipmentQuality.NORMAL, 50),
                    EquipmentQualityWeight(EquipmentQuality.FINE, 32),
                    EquipmentQualityWeight(EquipmentQuality.SUPERIOR, 14),
                    EquipmentQualityWeight(EquipmentQuality.EXQUISITE, 4),
                )
            ),
            QualityDistribution(
                playerLevelRange = 13..20,
                weights = listOf(
                    EquipmentQualityWeight(EquipmentQuality.NORMAL, 26),
                    EquipmentQualityWeight(EquipmentQuality.FINE, 37),
                    EquipmentQualityWeight(EquipmentQuality.SUPERIOR, 22),
                    EquipmentQualityWeight(EquipmentQuality.EXQUISITE, 10),
                    EquipmentQualityWeight(EquipmentQuality.FLAWLESS, 5),
                )
            ),
            QualityDistribution(
                playerLevelRange = 21..Int.MAX_VALUE,
                weights = listOf(
                    EquipmentQualityWeight(EquipmentQuality.NORMAL, 19),
                    EquipmentQualityWeight(EquipmentQuality.FINE, 30),
                    EquipmentQualityWeight(EquipmentQuality.SUPERIOR, 26),
                    EquipmentQualityWeight(EquipmentQuality.EXQUISITE, 16),
                    EquipmentQualityWeight(EquipmentQuality.FLAWLESS, 9),
                )
            )
        )
    }

}