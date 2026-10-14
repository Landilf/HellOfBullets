package ru.landilf.hellofbullets.domain.engine.equipment

import javax.inject.Inject

class EquipmentLevelUpgradeCostCalculator @Inject constructor() {
    operator fun invoke(
        baseCost: Int,
        targetLevel: Int
    ): Int {
        require(baseCost >= 0) {
            "Базовая стоимость улучшения не может быть отрицательной"
        }
        require(targetLevel in MIN_TARGET_LEVEL..MAX_TARGET_LEVEL) {
            "Целевой уровень должен быть от $MIN_TARGET_LEVEL до $MAX_TARGET_LEVEL"
        }

        return (baseCost + getLevelTierBonus(targetLevel)) * targetLevel
    }

    private fun getLevelTierBonus(
        targetLevel: Int
    ): Int {
        return when (targetLevel) {
            in 2..5 -> 0
            in 6..10 -> 1
            in 11..15 -> 3
            in 16..20 -> 5
            in 21..25 -> 7
            in 26..30 -> 10
            in 31..35 -> 12
            in 36..40 -> 14
            in 41..45 -> 17
            in 46..50 -> 20
            else -> error("Уровень уже проверен")
        }
    }

    private companion object {
        const val MIN_TARGET_LEVEL = 2
        const val MAX_TARGET_LEVEL = 50
    }
}