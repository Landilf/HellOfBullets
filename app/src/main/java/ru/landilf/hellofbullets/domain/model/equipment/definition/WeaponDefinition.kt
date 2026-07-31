package ru.landilf.hellofbullets.domain.model.equipment.definition

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType

data class WeaponDefinition(
    override val id: Long,
    override val name: String,
    override val primaryFirstGrowthMultiplier: Float,
    override val primarySecondGrowthMultiplier: Float,
    val baseDamage: Float,
    val baseAttackSpeed: Float,
    val attackRange: Float
) : EquipmentDefinition() {
    override val primaryFirstStatType = EquipmentStatType.DAMAGE
    override val primarySecondStatType = EquipmentStatType.ATTACK_SPEED
}
