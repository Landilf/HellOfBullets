package ru.landilf.hellofbullets.domain.model.equipment.definition

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType

data class ArmorDefinition(
    override val id: Long,
    override val name: String,
    override val primaryFirstGrowthMultiplier: Float,
    override val primarySecondGrowthMultiplier: Float,
    val baseHp: Float,
    val baseDefense: Float
) : EquipmentDefinition() {
    override val primaryFirstStatType = EquipmentStatType.HP
    override val primarySecondStatType = EquipmentStatType.DEFENSE
}
