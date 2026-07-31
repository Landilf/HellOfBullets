package ru.landilf.hellofbullets.domain.model.equipment.definition

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType

data class ArtifactDefinition(
    override val id: Long,
    override val name: String,
    override val primaryFirstGrowthMultiplier: Float,
    override val primarySecondGrowthMultiplier: Float,
    val baseCooldownReductionPercent: Float,
    val baseDurationBonusPercent: Float
) : EquipmentDefinition() {
    override val primaryFirstStatType = EquipmentStatType.COOLDOWN_REDUCTION
    override val primarySecondStatType = EquipmentStatType.DURATION
}
