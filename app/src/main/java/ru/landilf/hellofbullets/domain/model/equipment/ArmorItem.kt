package ru.landilf.hellofbullets.domain.model.equipment

data class ArmorItem(
    override val id: Long,
    override val definitionId: Long,
    override val level: Int,
    override val quality: EquipmentQuality,
    override val additionalStatType: EquipmentStatType,
    override val additionalStatValue: Float,
    val hp: Float,
    val defense: Float,
    override val specializationCoef: Float
) : Item(specializationCoef)
