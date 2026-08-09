package ru.landilf.hellofbullets.presentation.equipment

import androidx.compose.ui.graphics.vector.ImageVector
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.player.EquipmentSlot
import ru.landilf.hellofbullets.presentation.common.equipment.EquipmentStatUiModel

data class EquipmentItemUiModel(
    val itemId: Long,
    val slot: EquipmentSlot,
    val itemName: String,
    val icon: ImageVector,
    val level: Int,
    val maxLevel: Int,
    val quality: EquipmentQuality,
    val primaryStats: List<EquipmentStatUiModel>,
    val additionalStat: EquipmentStatUiModel,
    val isEquipped: Boolean
)
