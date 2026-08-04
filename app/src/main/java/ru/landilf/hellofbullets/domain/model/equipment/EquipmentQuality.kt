package ru.landilf.hellofbullets.domain.model.equipment

enum class EquipmentQuality(
    val qualityLevel: Int,
    val purchasePriceMultiplier: Int,
    val isAvailableInShop: Boolean,
    val maxLevel: Int,
    val materialsRequiredForUpgrade: Int?
) {
    NORMAL(
        qualityLevel = 1,
        purchasePriceMultiplier = 1,
        isAvailableInShop = true,
        maxLevel = 20,
        materialsRequiredForUpgrade = 6
    ),
    FINE(
        qualityLevel = 2,
        purchasePriceMultiplier = 2,
        isAvailableInShop = true,
        maxLevel = 25,
        materialsRequiredForUpgrade = 5
    ),
    SUPERIOR(
        qualityLevel = 3,
        purchasePriceMultiplier = 4,
        isAvailableInShop = true,
        maxLevel = 30,
        materialsRequiredForUpgrade = 4
    ),
    EXQUISITE(
        qualityLevel = 4,
        purchasePriceMultiplier = 7,
        isAvailableInShop = true,
        maxLevel = 35,
        materialsRequiredForUpgrade = 3
    ),
    FLAWLESS(
        qualityLevel = 5,
        purchasePriceMultiplier = 11,
        isAvailableInShop = true,
        maxLevel = 40,
        materialsRequiredForUpgrade = 2
    ),
    EPIC(
        qualityLevel = 6,
        purchasePriceMultiplier = 16,
        isAvailableInShop = false,
        maxLevel = 45,
        materialsRequiredForUpgrade = 1
    ),
    LEGENDARY(
        qualityLevel = 7,
        purchasePriceMultiplier = 22,
        isAvailableInShop = false,
        maxLevel = 50,
        materialsRequiredForUpgrade = null
    );

    val nextQuality: EquipmentQuality?
        get() = entries.getOrNull(ordinal + 1)
}