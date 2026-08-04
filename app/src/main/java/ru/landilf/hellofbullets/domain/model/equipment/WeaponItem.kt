package ru.landilf.hellofbullets.domain.model.equipment

data class WeaponItem(
    override val id: Long,
    override val definitionId: Long,
    override val level: Int,
    override val quality: EquipmentQuality,
    override val additionalStatType: EquipmentStatType,
    override val additionalStatValue: Float,
    val damage: Float,
    val attackSpeed: Float,
    override val specializationCoef: Float
) : Item(specializationCoef)
