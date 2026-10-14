package ru.landilf.hellofbullets.domain.repository

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.definition.AdditionalStatConfig
import ru.landilf.hellofbullets.domain.model.equipment.definition.StatRange

interface EquipmentStatConfigRepository {
    fun getReferenceRange(statType: EquipmentStatType): StatRange
    fun getAdditionalStatConfig(statType: EquipmentStatType): AdditionalStatConfig
}