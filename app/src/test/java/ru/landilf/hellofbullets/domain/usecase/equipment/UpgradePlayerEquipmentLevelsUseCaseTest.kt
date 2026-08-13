package ru.landilf.hellofbullets.domain.usecase.equipment

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.engine.equipment.EquipmentLevelUpgradeCostCalculator
import ru.landilf.hellofbullets.domain.engine.equipment.EquipmentLevelUpgradeStatCalculator
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.createWeapon
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.weaponDefinition
import ru.landilf.hellofbullets.domain.fixtures.FLOAT_EPSILON
import ru.landilf.hellofbullets.domain.fixtures.FakeEquipmentStatConfigRepository
import ru.landilf.hellofbullets.domain.fixtures.PlayerTestFixtures.createPlayerState
import ru.landilf.hellofbullets.domain.generator.EquipmentRandomGenerator
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentStatSource
import ru.landilf.hellofbullets.domain.usecase.FakeEquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository

class UpgradePlayerEquipmentLevelsUseCaseTest {

    @Test
    fun `upgrades several regular levels and returns every step`() = runBlocking {
        val weapon = createWeapon(level = 1)
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                silverAmount = 100,
                items = listOf(weapon)
            )
        )
        val useCase = createUseCase(playerRepository)

        val result = useCase(
            itemId = weapon.id,
            levelsToUpgrade = 3
        )

        val upgradedItem = result.upgradedItem as WeaponItem

        assertEquals(90, result.spentSilver)
        assertEquals(4, upgradedItem.level)
        assertEquals(14.5f, upgradedItem.damage, FLOAT_EPSILON)
        assertEquals(10, playerRepository.state?.playerProfile?.silverAmount)
        assertEquals(listOf(2, 3, 4), result.steps.map { it.targetLevel })
        assertEquals(
            listOf(
                EquipmentStatSource.PRIMARY_FIRST,
                EquipmentStatSource.PRIMARY_FIRST,
                EquipmentStatSource.PRIMARY_FIRST
            ),
            result.steps.map { step ->
                step.statChanges.single().source
            }
        )
        assertEquals(
            10f,
            result.steps.first().statChanges.single().previousValue,
            FLOAT_EPSILON
        )
        assertEquals(
            14.5f,
            result.steps.last().statChanges.single().updatedValue,
            FLOAT_EPSILON
        )
    }

    @Test
    fun `records randomly selected additional stat on fifth level`() = runBlocking {
        val weapon = createWeapon(
            level = 4,
            additionalStatType = EquipmentStatType.DAMAGE
        )
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                silverAmount = 100,
                items = listOf(weapon)
            )
        )
        val useCase = createUseCase(
            playerRepository = playerRepository,
            equipmentRandomGenerator = FakeEquipmentRandomGenerator(
                nextIntValue = 1
            )
        )

        val result = useCase(
            itemId = weapon.id,
            levelsToUpgrade = 1
        )

        val step = result.steps.single()
        val upgradedItem = result.upgradedItem as WeaponItem

        assertEquals(5, step.targetLevel)
        assertEquals(
            listOf(
                EquipmentStatSource.PRIMARY_FIRST,
                EquipmentStatSource.ADDITIONAL
            ),
            step.statChanges.map { it.source }
        )
        assertEquals(11.5f, upgradedItem.damage, FLOAT_EPSILON)
        assertEquals(3.3f, upgradedItem.additionalStatValue, FLOAT_EPSILON)
    }

    private fun createUseCase(
        playerRepository: FakePlayerRepository,
        equipmentRandomGenerator: FakeEquipmentRandomGenerator = FakeEquipmentRandomGenerator()
    ): UpgradePlayerEquipmentLevelsUseCase {
        return UpgradePlayerEquipmentLevelsUseCase(
            playerRepository = playerRepository,
            equipmentDefinitionRepository = FakeEquipmentDefinitionRepository(
                initialDefinitions = listOf(weaponDefinition)
            ),
            equipmentLevelUpgradeCostCalculator = EquipmentLevelUpgradeCostCalculator(),
            upgradeEquipmentLevelUseCase = UpgradeEquipmentLevelUseCase(
                equipmentLevelUpgradeStatCalculator = EquipmentLevelUpgradeStatCalculator(
                    equipmentStatConfigRepository = FakeEquipmentStatConfigRepository()
                )
            ),
            equipmentRandomGenerator = equipmentRandomGenerator
        )
    }

    private class FakeEquipmentRandomGenerator(
        private val nextIntValue: Int = 0
    ) : EquipmentRandomGenerator {
        var nextIntCallCount = 0
            private set

        override fun nextFloat(from: Float, until: Float): Float {
            error("nextFloat не должен вызываться при повышении уровня")
        }

        override fun nextInt(until: Int): Int {
            nextIntCallCount++
            return nextIntValue
        }

    }
}