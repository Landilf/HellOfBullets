package ru.landilf.hellofbullets.domain.model.equipment.definition

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType

sealed class EquipmentDefinition {
    abstract val id: Long
    abstract val name: String
    abstract val primaryFirstStatType: EquipmentStatType
    abstract val primarySecondStatType: EquipmentStatType
    abstract val primaryFirstGrowthMultiplier: Float
    abstract val primarySecondGrowthMultiplier: Float

    fun growthMultiplierFor(
        statType: EquipmentStatType
    ): Float {
        return when (statType) {
            primaryFirstStatType -> primaryFirstGrowthMultiplier
            primarySecondStatType -> primarySecondGrowthMultiplier
            else -> FOREIGN_STAT_GROWTH_MULTIPLIER
        }
    }

    private companion object {
        const val FOREIGN_STAT_GROWTH_MULTIPLIER = 0.5f
    }
}