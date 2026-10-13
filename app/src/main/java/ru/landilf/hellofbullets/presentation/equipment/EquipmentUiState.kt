package ru.landilf.hellofbullets.presentation.equipment

data class EquipmentUiState(
    val isLoading: Boolean = true,
    val weaponItems: List<EquipmentItemUiModel> = emptyList(),
    val armorItems: List<EquipmentItemUiModel> = emptyList(),
    val artifactItems: List<EquipmentItemUiModel> = emptyList(),
    val selectedItemId: Long? = null,
    val isItemDetailsVisible: Boolean = false,
    val equippedWeaponItemId: Long? = null,
    val equippedArmorItemId: Long? = null,
    val equippedArtifactItemId: Long? = null,
    val levelUpgradeOverlay: EquipmentLevelUpgradeOverlayUiState? = null,
    val qualityUpgradeOverlay: EquipmentQualityUpgradeOverlayUiState? = null,
    val isQualityUpgradeInProgress: Boolean = false,
    val isLevelUpgradeInProgress: Boolean = false,
    val levelUpgradeResult: EquipmentLevelUpgradeResultUiState? = null,
    val currentLevelUpgradeStepIndex: Int = 0,
    val isLevelUpgradeResultAnimating: Boolean = false,
    val levelUpgradeStepDurationMs: Int = EQUIPMENT_LEVEL_UPGRADE_STEP_DURATION_MS,
    val errorMessage: String? = null
) {
    fun selectedItem(): EquipmentItemUiModel? {
        val selectedItemId = selectedItemId ?: return null

        return allItems().firstOrNull { item ->
            item.itemId == selectedItemId
        }
    }

    fun allItems(): List<EquipmentItemUiModel> {
        return weaponItems + armorItems + artifactItems
    }
}