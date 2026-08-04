package ru.landilf.hellofbullets.domain.repository

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQualityWeight

interface EquipmentQualityDistributionRepository {
    fun getWeightsForPlayerLevel(playerLevel: Int): List<EquipmentQualityWeight>
}