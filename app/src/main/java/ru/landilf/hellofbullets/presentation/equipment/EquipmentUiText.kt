package ru.landilf.hellofbullets.presentation.equipment

import androidx.annotation.StringRes
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import java.util.Locale

@StringRes
fun EquipmentQuality.toStringRes(): Int {
    return when (this) {
        EquipmentQuality.NORMAL -> R.string.equipment_quality_normal
        EquipmentQuality.FINE -> R.string.equipment_quality_fine
        EquipmentQuality.SUPERIOR -> R.string.equipment_quality_superior
        EquipmentQuality.EXQUISITE -> R.string.equipment_quality_exquisite
        EquipmentQuality.FLAWLESS -> R.string.equipment_quality_flawless
        EquipmentQuality.EPIC -> R.string.equipment_quality_epic
        EquipmentQuality.LEGENDARY -> R.string.equipment_quality_legendary
    }
}

@StringRes
fun EquipmentStatType.toStringRes(): Int {
    return when (this) {
        EquipmentStatType.DAMAGE -> R.string.equipment_stat_damage
        EquipmentStatType.ATTACK_SPEED -> R.string.equipment_stat_attack_speed
        EquipmentStatType.HP -> R.string.equipment_stat_hp
        EquipmentStatType.DEFENSE -> R.string.equipment_stat_defense
        EquipmentStatType.COOLDOWN_REDUCTION -> R.string.equipment_stat_cooldown_reduction
        EquipmentStatType.DURATION -> R.string.equipment_stat_duration
    }
}

fun EquipmentStatType.formatValue(value: Float): String {
    val formattedValue = String.format(
        Locale.getDefault(),
        "%.1f",
        value
    )

    return when (this) {
        EquipmentStatType.COOLDOWN_REDUCTION,
        EquipmentStatType.DURATION -> "$formattedValue%"

        else -> formattedValue
    }
}