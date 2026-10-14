package ru.landilf.hellofbullets.presentation.common.equipment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType

fun EquipmentStatType.toIcon(): ImageVector {
    return when (this) {
        EquipmentStatType.DAMAGE -> Icons.Outlined.GpsFixed
        EquipmentStatType.ATTACK_SPEED -> Icons.Outlined.Speed
        EquipmentStatType.HP -> Icons.Outlined.FavoriteBorder
        EquipmentStatType.DEFENSE -> Icons.Outlined.Shield
        EquipmentStatType.COOLDOWN_REDUCTION -> Icons.Outlined.Timer
        EquipmentStatType.DURATION -> Icons.Outlined.HourglassBottom
    }
}