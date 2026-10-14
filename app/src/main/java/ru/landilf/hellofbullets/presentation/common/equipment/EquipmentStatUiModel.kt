package ru.landilf.hellofbullets.presentation.common.equipment

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType

data class EquipmentStatUiModel(
    val type: EquipmentStatType,
    val value: Float
)