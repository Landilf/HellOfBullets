package ru.landilf.hellofbullets.domain.model.equipment.definition

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType

data class ArmorDefinition(
    override val id: Long,
    override val name: String,
    override val primaryFirstGrowthMultiplier: Float,
    override val primarySecondGrowthMultiplier: Float,
    override val baseLevelUpgradeCost: Int,
    val hpRange: StatRange,
    val defenseRange: StatRange
) : EquipmentDefinition() {
    override val primaryFirstStatType = EquipmentStatType.HP
    override val primarySecondStatType = EquipmentStatType.DEFENSE
    override val primaryFirstStatRange: StatRange
        get() = hpRange
    override val primarySecondStatRange: StatRange
        get() = defenseRange
}
