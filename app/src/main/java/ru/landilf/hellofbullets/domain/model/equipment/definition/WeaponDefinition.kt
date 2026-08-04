package ru.landilf.hellofbullets.domain.model.equipment.definition

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType

data class WeaponDefinition(
    override val id: Long,
    override val name: String,
    override val primaryFirstGrowthMultiplier: Float,
    override val primarySecondGrowthMultiplier: Float,
    override val basePurchasePrice: Int,
    override val baseLevelUpgradeCost: Int,
    val damageRange: StatRange,
    val attackSpeedRange: StatRange,
    val attackRange: Float
) : EquipmentDefinition() {
    override val primaryFirstStatType = EquipmentStatType.DAMAGE
    override val primarySecondStatType = EquipmentStatType.ATTACK_SPEED
    override val primaryFirstStatRange: StatRange
        get() = damageRange
    override val primarySecondStatRange: StatRange
        get() = attackSpeedRange
}
