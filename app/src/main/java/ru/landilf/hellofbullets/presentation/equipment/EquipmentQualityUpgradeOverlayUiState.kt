package ru.landilf.hellofbullets.presentation.equipment

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType

data class EquipmentQualityUpgradeOverlayUiState(
    val itemId: Long,
    val currentQuality: EquipmentQuality,
    val nextQuality: EquipmentQuality,
    val requiredMaterialsCount: Int,
    val availableMaterialsCount: Int,
    val materialItems: List<EquipmentItemUiModel>,
    val selectedMaterialItemIds: Set<Long> = emptySet(),
    val primaryFirstStatPreview: EquipmentQualityUpgradeStatPreviewUiModel
) {
    val selectedMaterialsCount: Int
        get() = selectedMaterialItemIds.size

    val canConfirm: Boolean
        get() = selectedMaterialsCount == requiredMaterialsCount
}

data class EquipmentQualityUpgradeStatPreviewUiModel(
    val statType: EquipmentStatType,
    val currentValue: Float,
    val upgradedValue: Float
)
