package ru.landilf.hellofbullets.domain.model.equipment

sealed class Item {
    abstract val id: Long
    abstract val definitionId: Long
    abstract val level: Int
    abstract val quality: EquipmentQuality
    abstract val additionalStatType: EquipmentStatType
    abstract val additionalStatValue: Float

    val maxLevel: Int
        get() = quality.maxLevel
}