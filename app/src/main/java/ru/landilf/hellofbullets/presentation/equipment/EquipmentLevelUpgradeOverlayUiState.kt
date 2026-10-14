package ru.landilf.hellofbullets.presentation.equipment

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentStatSource

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

data class EquipmentLevelUpgradeResultUiState(
    val originalLevel: Int,
    val upgradedLevel: Int,
    val spentSilver: Int,
    val steps: List<EquipmentLevelUpgradeStepUiModel>
)

data class EquipmentLevelUpgradeStepUiModel(
    val targetLevel: Int,
    val statChanges: List<EquipmentStatUpgradeUiModel>
)

data class EquipmentStatUpgradeUiModel(
    val statType: EquipmentStatType,
    val currentValue: Float,
    val increment: Float,
    val source: EquipmentStatSource
)
