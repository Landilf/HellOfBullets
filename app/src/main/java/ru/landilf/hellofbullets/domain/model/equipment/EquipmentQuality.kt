package ru.landilf.hellofbullets.domain.model.equipment

enum class EquipmentQuality(
    val qualityLevel: Int,
    val maxLevel: Int,
    val materialsRequiredForUpgrade: Int?
) {
    NORMAL(
        qualityLevel = 1,
        maxLevel = 20,
        materialsRequiredForUpgrade = 6
    ),
    FINE(
        qualityLevel = 2,
        maxLevel = 25,
        materialsRequiredForUpgrade = 5
    ),
    SUPERIOR(
        qualityLevel = 3,
        maxLevel = 30,
        materialsRequiredForUpgrade = 4
    ),
    EXQUISITE(
        qualityLevel = 4,
        maxLevel = 35,
        materialsRequiredForUpgrade = 3
    ),
    FLAWLESS(
        qualityLevel = 5,
        maxLevel = 40,
        materialsRequiredForUpgrade = 2
    ),
    EPIC(
        qualityLevel = 6,
        maxLevel = 45,
        materialsRequiredForUpgrade = 1
    ),
    LEGENDARY(
        qualityLevel = 7,
        maxLevel = 50,
        materialsRequiredForUpgrade = null
    )
}