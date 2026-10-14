package ru.landilf.hellofbullets.domain.engine.equipment


import org.junit.Assert.assertEquals
import org.junit.Test

class EquipmentLevelUpgradeCostCalculatorTest {
    private val calculator = EquipmentLevelUpgradeCostCalculator()

    @Test
    fun `calculates cost without bonus on early levels`() {
        assertEquals(
            20, calculator(
                baseCost = 10,
                targetLevel = 2
            )
        )
    }

    @Test
    fun `applies bonus for target level tier`() {
        assertEquals(
            66, calculator(
                baseCost = 10,
                targetLevel = 6
            )
        )
    }

    @Test
    fun `applies correct bonus on maximum level`() {
        assertEquals(
            1500, calculator(
                baseCost = 10,
                targetLevel = 50
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws for target level below upgrade range`() {
        calculator(
            baseCost = 10,
            targetLevel = 1
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws for negative base cost`() {
        calculator(
            baseCost = -1,
            targetLevel = 2
        )
    }
}