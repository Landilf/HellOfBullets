package ru.landilf.hellofbullets.domain.model.equipment

sealed class Item(
    open val specializationCoef: Float
) {
    init {
        require(specializationCoef in -1f..1f) {
            "Коэффициент специализации должен быть в диапазоне от -1 до 1"
        }
    }

    abstract val id: Long
    abstract val definitionId: Long
    abstract val level: Int
    abstract val quality: EquipmentQuality
    abstract val additionalStatType: EquipmentStatType
    abstract val additionalStatValue: Float

    val maxLevel: Int
        get() = quality.maxLevel
}