package ru.landilf.hellofbullets.presentation.shop

import androidx.compose.ui.graphics.vector.ImageVector
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType

data class ShopOfferUiModel(
    val itemId: Long,
    val itemName: String,
    val icon: ImageVector,
    val level: Int,
    val quality: EquipmentQuality,
    val primaryStats: List<EquipmentStatUiModel>,
    val additionalStat: EquipmentStatUiModel,
    val purchasePrice: Int,
    val isSold: Boolean
)

data class EquipmentStatUiModel(
    val type: EquipmentStatType,
    val value: Float
)
