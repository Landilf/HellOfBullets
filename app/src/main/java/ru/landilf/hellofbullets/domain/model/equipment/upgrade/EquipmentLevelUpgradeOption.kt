package ru.landilf.hellofbullets.domain.model.equipment.upgrade

data class EquipmentLevelUpgradeOptions(
    val itemId: Long,
    val currentLevel: Int,
    val maxLevel: Int,
    val availableSilver: Int,
    val options: List<EquipmentLevelUpgradeOption>
)

data class EquipmentLevelUpgradeOption(
    val levelsToUpgrade: Int,
    val targetLevel: Int,
    val totalCost: Int,
    val guaranteedStatChanges: List<EquipmentStatChange>,
    val randomUpgradeLevels: List<Int>
)