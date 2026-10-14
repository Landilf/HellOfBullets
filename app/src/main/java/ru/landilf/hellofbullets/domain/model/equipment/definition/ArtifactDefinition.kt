package ru.landilf.hellofbullets.domain.model.equipment.definition

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType

data class ArtifactDefinition(
    override val id: Long,
    override val name: String,
    override val primaryFirstGrowthMultiplier: Float,
    override val primarySecondGrowthMultiplier: Float,
    override val basePurchasePrice: Int,
    override val baseLevelUpgradeCost: Int,
    val cooldownReductionPercentRange: StatRange,
    val durationBonusPercentRange: StatRange
) : EquipmentDefinition() {
    override val primaryFirstStatType = EquipmentStatType.COOLDOWN_REDUCTION
    override val primarySecondStatType = EquipmentStatType.DURATION
    override val primaryFirstStatRange: StatRange
        get() = cooldownReductionPercentRange
    override val primarySecondStatRange: StatRange
        get() = durationBonusPercentRange
}
