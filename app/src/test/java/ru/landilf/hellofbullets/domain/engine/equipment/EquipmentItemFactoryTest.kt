package ru.landilf.hellofbullets.domain.engine.equipment

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.weaponDefinition
import ru.landilf.hellofbullets.domain.fixtures.FLOAT_EPSILON
import ru.landilf.hellofbullets.domain.generator.EquipmentItemIdGenerator
import ru.landilf.hellofbullets.domain.generator.EquipmentRandomGenerator
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.equipment.definition.AdditionalStatConfig
import ru.landilf.hellofbullets.domain.model.equipment.definition.StatRange
import ru.landilf.hellofbullets.domain.repository.EquipmentStatConfigRepository

class EquipmentItemFactoryTest {
    @Test
    fun `creates specialized weapon with matching additional stat`() = runBlocking {
        val factory = createFactory(
            floatValues = listOf(1f, 0f),
            intValues = listOf(indexOf(EquipmentStatType.DAMAGE))
        )

        val item = factory(
            definition = weaponDefinition,
            quality = EquipmentQuality.FINE
        ) as WeaponItem

        assertEquals(42L, item.id)
        assertEquals(EquipmentQuality.FINE, item.quality)
        assertEquals(1f, item.specializationCoef, FLOAT_EPSILON)
        assertEquals(12.1f, item.damage, FLOAT_EPSILON)
        assertEquals(1.8f, item.attackSpeed, FLOAT_EPSILON)
        assertEquals(EquipmentStatType.DAMAGE, item.additionalStatType)
        assertEquals(5f, item.additionalStatValue, FLOAT_EPSILON)
    }

    @Test
    fun `uses reference range for foreign additional stat`() = runBlocking {
        val factory = createFactory(
            floatValues = listOf(0f, 0f),
            intValues = listOf(indexOf(EquipmentStatType.HP))
        )

        val item = factory(definition = weaponDefinition) as WeaponItem

        assertEquals(10f, item.damage, FLOAT_EPSILON)
        assertEquals(2f, item.attackSpeed, FLOAT_EPSILON)
        assertEquals(EquipmentStatType.HP, item.additionalStatType)
        assertEquals(25f, item.additionalStatValue, FLOAT_EPSILON)
    }

    private fun createFactory(
        floatValues: List<Float>,
        intValues: List<Int>
    ): EquipmentItemFactory {
        return EquipmentItemFactory(
            equipmentItemIdGenerator = FakeEquipmentItemIdGenerator(),
            equipmentRandomGenerator = FakeEquipmentRandomGenerator(
                floatValues = floatValues,
                intValues = intValues
            ),
            equipmentStatConfigRepository = FakeEquipmentStatConfigRepository()
        )
    }

    private fun indexOf(statType: EquipmentStatType): Int {
        return EquipmentStatType.entries.indexOf(statType)
    }

    private class FakeEquipmentItemIdGenerator : EquipmentItemIdGenerator {
        override suspend fun generateId(): Long = 42
    }

    private class FakeEquipmentRandomGenerator(
        floatValues: List<Float>,
        intValues: List<Int>
    ) : EquipmentRandomGenerator {
        private val floats = ArrayDeque(floatValues)
        private val integers = ArrayDeque(intValues)

        override fun nextFloat(from: Float, until: Float): Float {
            return floats.removeFirst()
        }

        override fun nextInt(until: Int): Int {
            return integers.removeFirst()
        }
    }

    private class FakeEquipmentStatConfigRepository : EquipmentStatConfigRepository {
        override fun getReferenceRange(statType: EquipmentStatType): StatRange {
            return referenceRanges.getValue(statType)
        }

        override fun getAdditionalStatConfig(statType: EquipmentStatType): AdditionalStatConfig {
            return additionalStatConfig.getValue(statType)
        }
    }

    private companion object {
        val referenceRanges = mapOf(
            EquipmentStatType.HP to StatRange(80f, 120f)
        )

        val additionalStatConfig = mapOf(
            EquipmentStatType.DAMAGE to AdditionalStatConfig(
                initialValueCoef = 0.5f,
                levelGrowthMultiplier = 0.3f
            ),
            EquipmentStatType.HP to AdditionalStatConfig(
                initialValueCoef = 0.25f,
                levelGrowthMultiplier = 0.5f
            )
        )
    }
}