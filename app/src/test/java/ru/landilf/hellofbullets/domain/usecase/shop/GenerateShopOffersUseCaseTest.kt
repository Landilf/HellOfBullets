package ru.landilf.hellofbullets.domain.usecase.shop

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.landilf.hellofbullets.domain.engine.equipment.EquipmentItemFactory
import ru.landilf.hellofbullets.domain.engine.equipment.EquipmentPurchasePriceCalculator
import ru.landilf.hellofbullets.domain.engine.equipment.ShopEquipmentQualitySelector
import ru.landilf.hellofbullets.domain.generator.EquipmentItemIdGenerator
import ru.landilf.hellofbullets.domain.generator.EquipmentRandomGenerator
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQualityWeight
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.definition.AdditionalStatConfig
import ru.landilf.hellofbullets.domain.model.equipment.definition.EquipmentDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.StatRange
import ru.landilf.hellofbullets.domain.model.equipment.definition.WeaponDefinition
import ru.landilf.hellofbullets.domain.repository.EquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.repository.EquipmentQualityDistributionRepository
import ru.landilf.hellofbullets.domain.repository.EquipmentStatConfigRepository

class GenerateShopOffersUseCaseTest {
    @Test
    fun `generates sixteen offers with duplicate definitions and unique item ids`() = runBlocking {
        val offers = createUseCase()(playerLevel = 1)

        assertEquals(16, offers.size)
        assertTrue(offers.all { it.item.definitionId == pistolDefinition.id })
        assertTrue(offers.all { it.item.quality == EquipmentQuality.NORMAL })
        assertTrue(offers.all { it.purchasePrice == 100 })
        assertEquals(
            (1L..16L).toList(),
            offers.map { it.item.id }
        )
    }

    private fun createUseCase(): GenerateShopOffersUseCase {
        val randomGenerator = FakeEquipmentRandomGenerator()

        return GenerateShopOffersUseCase(
            equipmentDefinitionRepository = FakeEquipmentDefinitionRepository(),
            equipmentRandomGenerator = randomGenerator,
            shopEquipmentQualitySelector = ShopEquipmentQualitySelector(
                equipmentRandomGenerator = randomGenerator,
                equipmentQualityDistributionRepository = FakeEquipmentQualityDistributionRepository()
            ),
            equipmentItemFactory = EquipmentItemFactory(
                equipmentItemIdGenerator = FakeEquipmentItemIdGenerator(),
                equipmentRandomGenerator = randomGenerator,
                equipmentStatConfigRepository = FakeEquipmentStatConfigRepository()
            ),
            equipmentPurchasePriceCalculator = EquipmentPurchasePriceCalculator()
        )
    }

    private class FakeEquipmentItemIdGenerator : EquipmentItemIdGenerator {
        private var nextId = 1L

        override suspend fun generateId(): Long = nextId++
    }

    private class FakeEquipmentRandomGenerator : EquipmentRandomGenerator {
        override fun nextFloat(from: Float, until: Float): Float = 0f
        override fun nextInt(until: Int): Int = 0
    }

    private class FakeEquipmentDefinitionRepository : EquipmentDefinitionRepository {
        override fun getDefinitions(): List<EquipmentDefinition> {
            return listOf(pistolDefinition)
        }

        override fun getDefinitionById(id: Long): EquipmentDefinition? {
            return pistolDefinition.takeIf { it.id == id }
        }
    }

    private class FakeEquipmentQualityDistributionRepository :
        EquipmentQualityDistributionRepository {

        override fun getWeightsForPlayerLevel(
            playerLevel: Int
        ): List<EquipmentQualityWeight> {
            return listOf(
                EquipmentQualityWeight(
                    quality = EquipmentQuality.NORMAL,
                    weight = 100
                )
            )
        }
    }

    private class FakeEquipmentStatConfigRepository : EquipmentStatConfigRepository {
        override fun getReferenceRange(statType: EquipmentStatType): StatRange {
            error("Эталонный диапазон не должен использоваться в этом тесте")
        }

        override fun getAdditionalStatConfig(
            statType: EquipmentStatType
        ): AdditionalStatConfig {
            return AdditionalStatConfig(
                initialValueCoef = 0.5f,
                levelGrowthMultiplier = 0.3f
            )
        }
    }

    private companion object {
        val pistolDefinition = WeaponDefinition(
            id = 1L,
            name = "Pistol",
            primaryFirstGrowthMultiplier = 1.5f,
            primarySecondGrowthMultiplier = 0.25f,
            basePurchasePrice = 100,
            baseLevelUpgradeCost = 10,
            damageRange = StatRange(9f, 11f),
            attackSpeedRange = StatRange(1.8f, 2.2f),
            attackRange = 500f
        )
    }
}