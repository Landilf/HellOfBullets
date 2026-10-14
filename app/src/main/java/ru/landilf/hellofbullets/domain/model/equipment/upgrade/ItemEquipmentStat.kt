package ru.landilf.hellofbullets.domain.model.equipment.upgrade

import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem

fun Item.statValue(
    source: EquipmentStatSource
): Float {
    return when (source) {
        EquipmentStatSource.PRIMARY_FIRST -> when (this) {
            is WeaponItem -> damage
            is ArmorItem -> hp
            is ArtifactItem -> cooldownReductionPercent
        }

        EquipmentStatSource.PRIMARY_SECOND -> when (this) {
            is WeaponItem -> attackSpeed
            is ArmorItem -> defense
            is ArtifactItem -> durationBonusPercent
        }

        EquipmentStatSource.ADDITIONAL -> additionalStatValue
    }
}