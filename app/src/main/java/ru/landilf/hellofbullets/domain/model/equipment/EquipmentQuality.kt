package ru.landilf.hellofbullets.domain.model.equipment

enum class EquipmentQuality(
    val qualityLevel: Int,
    val purchasePriceMultiplier: Int,
    val isAvailableInShop: Boolean,
    val maxLevel: Int,
    val materialsRequiredForUpgrade: Int?,
    val primaryFirstStatQualityBonusCoef: Float
) {
    NORMAL(
        qualityLevel = 1,
        purchasePriceMultiplier = 1,
        isAvailableInShop = true,
        maxLevel = 20,
        materialsRequiredForUpgrade = 6,
        primaryFirstStatQualityBonusCoef = 0f
    ),
    FINE(
        qualityLevel = 2,
        purchasePriceMultiplier = 2,
        isAvailableInShop = true,
        maxLevel = 25,
        materialsRequiredForUpgrade = 5,
        primaryFirstStatQualityBonusCoef = 0.10f
    ),
    SUPERIOR(
        qualityLevel = 3,
        purchasePriceMultiplier = 4,
        isAvailableInShop = true,
        maxLevel = 30,
        materialsRequiredForUpgrade = 4,
        primaryFirstStatQualityBonusCoef = 0.25f
    ),
    EXQUISITE(
        qualityLevel = 4,
        purchasePriceMultiplier = 7,
        isAvailableInShop = true,
        maxLevel = 35,
        materialsRequiredForUpgrade = 3,
        primaryFirstStatQualityBonusCoef = 0.45f
    ),
    FLAWLESS(
        qualityLevel = 5,
        purchasePriceMultiplier = 11,
        isAvailableInShop = true,
        maxLevel = 40,
        materialsRequiredForUpgrade = 2,
        primaryFirstStatQualityBonusCoef = 0.70f
    ),
    EPIC(
        qualityLevel = 6,
        purchasePriceMultiplier = 16,
        isAvailableInShop = false,
        maxLevel = 45,
        materialsRequiredForUpgrade = 1,
        primaryFirstStatQualityBonusCoef = 1.00f
    ),
    LEGENDARY(
        qualityLevel = 7,
        purchasePriceMultiplier = 22,
        isAvailableInShop = false,
        maxLevel = 50,
        materialsRequiredForUpgrade = null,
        primaryFirstStatQualityBonusCoef = 1.35f
    );

    val nextQuality: EquipmentQuality?
        get() = entries.getOrNull(ordinal + 1)

    val primaryFirstStatQualityMultiplier: Float
        get() = 1f + primaryFirstStatQualityBonusCoef
}