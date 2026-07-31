package ru.landilf.hellofbullets.domain.model.equipment

data class ArtifactItem(
    override val id: Long,
    override val definitionId: Long,
    override val level: Int,
    override val quality: EquipmentQuality,
    override val additionalStatType: EquipmentStatType,
    override val additionalStatValue: Float,
    val cooldownReductionPercent: Float,
    val durationBonusPercent: Float
) : Item()
