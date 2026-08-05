package ru.landilf.hellofbullets.domain.usecase.shop

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import ru.landilf.hellofbullets.domain.engine.shop.ShopManualRefreshCostCalculator

class ShopManualRefreshCostCalculatorTest {
    private val calculator = ShopManualRefreshCostCalculator()

    @Test
    fun `calculates costs for all manual refreshes on first level`() {
        assertEquals(
            55, calculator(
                playerLevel = 1,
                completedRefreshCount = 0
            )
        )
        assertEquals(
            75, calculator(
                playerLevel = 1,
                completedRefreshCount = 1
            )
        )
        assertEquals(
            175, calculator(
                playerLevel = 1,
                completedRefreshCount = 2
            )
        )
    }

    @Test
    fun `calculates costs based on player level`() {
        assertEquals(
            95, calculator(
                playerLevel = 10,
                completedRefreshCount = 0
            )
        )
        assertEquals(
            275, calculator(
                playerLevel = 10,
                completedRefreshCount = 1
            )
        )
        assertEquals(
            1_175, calculator(
                playerLevel = 10,
                completedRefreshCount = 2
            )
        )
    }

    @Test
    fun `rejects nonpositive player level`() {
        try {
            calculator(
                playerLevel = 0,
                completedRefreshCount = 0
            )
            fail("Ожидалось IllegalArgumentException")
        } catch (exception: IllegalArgumentException) {
            assertEquals(
                "Уровень игрока должен быть положительным",
                exception.message
            )
        }
    }

    @Test
    fun `rejects refresh after daily limit`() {
        try {
            calculator(
                playerLevel = 1,
                completedRefreshCount = 3
            )
            fail("Ожидалось IllegalArgumentException")
        } catch (exception: IllegalArgumentException) {
            assertEquals(
                "Достигнуто дневное ограничение ручных обновлений магазина",
                exception.message
            )
        }
    }
}