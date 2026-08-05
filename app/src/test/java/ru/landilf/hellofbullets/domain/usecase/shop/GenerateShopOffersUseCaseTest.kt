package ru.landilf.hellofbullets.domain.usecase.shop

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality

class GenerateShopOffersUseCaseTest {
    @Test
    fun `generates sixteen offers with duplicate definitions and unique item ids`() = runBlocking {
        val offers = ShopTestFixtures.createGenerateShopOffersUseCase()(playerLevel = 1)

        assertEquals(16, offers.size)
        assertTrue(offers.all { it.item.definitionId == ShopTestFixtures.PISTOL_DEFINITION_ID })
        assertTrue(offers.all { it.item.quality == EquipmentQuality.NORMAL })
        assertTrue(offers.all { it.purchasePrice == 100 })
        assertEquals(
            (1L..16L).toList(),
            offers.map { it.item.id }
        )
    }
}