package ru.landilf.hellofbullets.presentation.equipment

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType

data class EquipmentLevelUpgradeOverlayUiState(
    val itemId: Long,
    val currentLevel: Int,
    val maxLevel: Int,
    val availableSilver: Int,
    val selectedLevelsToUpgrade: Int,
    val options: List<EquipmentLevelUpgradeOptionUiModel>
) {
    val selectedOption: EquipmentLevelUpgradeOptionUiModel
        get() = options.first {
            it.levelsToUpgrade == selectedLevelsToUpgrade
        }
}

data class EquipmentLevelUpgradeOptionUiModel(
    val levelsToUpgrade: Int,
    val targetLevel: Int,
    val totalCost: Int,
    val guaranteedStatChanges: List<EquipmentStatUpgradeUiModel>,
    val randomUpgradeLevels: List<Int>
)

data class EquipmentStatUpgradeUiModel(
    val statType: EquipmentStatType,
    val currentValue: Float,
    val increment: Float
)