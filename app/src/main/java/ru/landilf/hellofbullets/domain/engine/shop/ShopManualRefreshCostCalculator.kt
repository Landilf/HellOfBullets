package ru.landilf.hellofbullets.domain.engine.shop

import ru.landilf.hellofbullets.domain.model.shop.ShopState
import javax.inject.Inject

class ShopManualRefreshCostCalculator @Inject constructor() {
    operator fun invoke(
        playerLevel: Int,
        completedRefreshCount: Int
    ): Int {
        require(playerLevel > 0) {
            "Уровень игрока должен быть положительным"
        }
        require(completedRefreshCount in 0 until ShopState.MAX_MANUAL_REFRESH_COUNT) {
            "Достигнуто дневное ограничение ручных обновлений магазина"
        }

        val nextRefreshNumber = completedRefreshCount + 1
        val growthMultiplier = calculateGrowthMultiplier(nextRefreshNumber)
        val levelMultiplier = maxOf(1, playerLevel - 1)

        return BASE_COST + growthMultiplier * levelMultiplier
    }

    private fun calculateGrowthMultiplier(
        refreshNumber: Int
    ): Int {
        var multiplier = 1

        repeat(refreshNumber) {
            multiplier *= GROWTH_BASE
        }

        return multiplier
    }

    private companion object {
        const val BASE_COST = 50
        const val GROWTH_BASE = 5
    }
}