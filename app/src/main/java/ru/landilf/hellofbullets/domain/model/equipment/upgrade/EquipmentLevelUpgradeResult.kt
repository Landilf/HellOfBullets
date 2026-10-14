package ru.landilf.hellofbullets.domain.model.equipment.upgrade

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.Item

data class EquipmentLevelUpgradeResult(
    val originalItem: Item,
    val upgradedItem: Item,
    val spentSilver: Int,
    val steps: List<EquipmentLevelUpgradeStep>
)

data class EquipmentLevelUpgradeStep(
    val targetLevel: Int,
    val statChanges: List<EquipmentStatChange>
)

data class EquipmentStatChange(
    val source: EquipmentStatSource,
    val statType: EquipmentStatType,
    val previousValue: Float,
    val updatedValue: Float
)
