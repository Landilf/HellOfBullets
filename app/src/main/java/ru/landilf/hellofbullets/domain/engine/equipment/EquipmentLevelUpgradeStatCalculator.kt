package ru.landilf.hellofbullets.domain.engine.equipment

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.definition.EquipmentDefinition
import ru.landilf.hellofbullets.domain.repository.EquipmentStatConfigRepository
import javax.inject.Inject

class EquipmentLevelUpgradeStatCalculator @Inject constructor(
    private val equipmentStatConfigRepository: EquipmentStatConfigRepository
) {
    fun primaryFirstIncrement(
        item: Item,
        definition: EquipmentDefinition,
        targetLevel: Int
    ): Float {
        return baseIncrementFor(targetLevel) *
                definition.primaryFirstGrowthMultiplierFor(item.specializationCoef) *
                item.quality.primaryFirstStatQualityMultiplier
    }

    fun primarySecondIncrement(
        item: Item,
        definition: EquipmentDefinition,
        targetLevel: Int
    ): Float {
        return baseIncrementFor(targetLevel) *
                definition.primarySecondGrowthMultiplierFor(item.specializationCoef)
    }

    fun additionalStatIncrement(
        statType: EquipmentStatType,
        targetLevel: Int
    ): Float {
        return baseIncrementFor(targetLevel) * equipmentStatConfigRepository
            .getAdditionalStatConfig(statType)
            .levelGrowthMultiplier
    }

    private fun baseIncrementFor(
        targetLevel: Int
    ): Float {
        return when (targetLevel) {
            in 1..10 -> 1f
            in 11..20 -> 5f
            in 21..30 -> 10f
            in 31..40 -> 15f
            in 41..50 -> 20f
            else -> error("Не определён базовый прирост для уровня $targetLevel")
        }
    }
}