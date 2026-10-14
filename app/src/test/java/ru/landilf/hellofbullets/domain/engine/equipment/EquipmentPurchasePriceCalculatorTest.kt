package ru.landilf.hellofbullets.domain.engine.equipment

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality

class EquipmentPurchasePriceCalculatorTest {
    private val calculator = EquipmentPurchasePriceCalculator()

    @Test
    fun `returns base price for normal quality`() {
        assertEquals(
            100,
            calculator(
                basePurchasePrice = 100,
                quality = EquipmentQuality.NORMAL
            )
        )
    }

    @Test
    fun `applies quality multiplier to base price`() {
        assertEquals(
            1_100,
            calculator(
                basePurchasePrice = 100,
                quality = EquipmentQuality.FLAWLESS
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws for non-positive base price`() {
        calculator(
            basePurchasePrice = 0,
            quality = EquipmentQuality.NORMAL
        )
    }
}