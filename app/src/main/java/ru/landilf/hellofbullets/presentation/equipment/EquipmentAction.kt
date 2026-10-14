package ru.landilf.hellofbullets.presentation.equipment

sealed interface EquipmentAction {
    data class OnItemClick(
        val itemId: Long
    ) : EquipmentAction

    data class OnQualityUpgradeMaterialClick(
        val itemId: Long
    ) : EquipmentAction

    data object OnItemDetailsDismiss : EquipmentAction
    data object OnToggleEquipmentClick : EquipmentAction
    data object OnLevelUpgradeClick : EquipmentAction
    data object OnLevelUpgradeOverlayDismiss : EquipmentAction
    data object OnLevelUpgradeLevelsIncrease : EquipmentAction
    data object OnLevelUpgradeLevelsDecrease : EquipmentAction
    data object OnLevelUpgradeConfirmClick : EquipmentAction
    data object OnLevelUpgradeAnimationAccelerate : EquipmentAction
    data object OnQualityUpgradeClick : EquipmentAction
    data object OnQualityUpgradeConfirmClick : EquipmentAction
    data object OnQualityUpgradeOverlayDismiss : EquipmentAction
}