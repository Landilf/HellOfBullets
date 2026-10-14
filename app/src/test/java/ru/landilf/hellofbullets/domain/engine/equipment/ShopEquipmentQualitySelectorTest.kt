package ru.landilf.hellofbullets.domain.engine.equipment

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.generator.EquipmentRandomGenerator
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQualityWeight
import ru.landilf.hellofbullets.domain.repository.EquipmentQualityDistributionRepository

class ShopEquipmentQualitySelectorTest {
    @Test
    fun `returns first quality for random weight inside first interval`() {
        val selector = createSelector(randomWeight = 69)

        val quality = selector(playerLevel = 1)

        assertEquals(EquipmentQuality.NORMAL, quality)
    }

    @Test
    fun `returns next quality at interval boundary`() {
        val selector = createSelector(randomWeight = 70)

        val quality = selector(playerLevel = 1)

        assertEquals(EquipmentQuality.FINE, quality)
    }

    private fun createSelector(
        randomWeight: Int
    ): ShopEquipmentQualitySelector {
        return ShopEquipmentQualitySelector(
            equipmentRandomGenerator = FakeEquipmentRandomGenerator(randomWeight),
            equipmentQualityDistributionRepository = FakeEquipmentQualityDistributionRepository()
        )
    }

    private class FakeEquipmentRandomGenerator(
        private val randomWeight: Int
    ) : EquipmentRandomGenerator {
        override fun nextFloat(
            from: Float,
            until: Float
        ): Float {
            error("Случайное значение Float не должно использоваться в этом месте")
        }

        override fun nextInt(until: Int): Int = randomWeight
    }

    private class FakeEquipmentQualityDistributionRepository :
        EquipmentQualityDistributionRepository {

        override fun getWeightsForPlayerLevel(
            playerLevel: Int
        ): List<EquipmentQualityWeight> {
            return listOf(
                EquipmentQualityWeight(EquipmentQuality.NORMAL, 70),
                EquipmentQualityWeight(EquipmentQuality.FINE, 30)
            )
        }
    }
}