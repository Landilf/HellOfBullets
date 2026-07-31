package ru.landilf.hellofbullets.domain.model.equipment

data class EquipmentQualityUpgradeResult(
    val upgradedItem: Item,
    val consumedMaterialIds: List<Long>
)
