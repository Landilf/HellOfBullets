package ru.landilf.hellofbullets.domain.model.equipment

data class EquipmentQualityUpgradeStatPreview(
    val statType: EquipmentStatType,
    val currentValue: Float,
    val upgradedValue: Float
)

sealed interface EquipmentQualityUpgradeOptions {
    data class Available(
        val itemId: Long,
        val currentQuality: EquipmentQuality,
        val nextQuality: EquipmentQuality,
        val primaryFirstStatPreview: EquipmentQualityUpgradeStatPreview,
        val requiredMaterialsCount: Int,
        val eligibleMaterialItemIds: List<Long>
    ) : EquipmentQualityUpgradeOptions

    data class Unavailable(
        val currentQuality: EquipmentQuality
    ) : EquipmentQualityUpgradeOptions
}