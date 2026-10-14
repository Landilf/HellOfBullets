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
import ru.landilf.hellofbullets.domain.usecase.FakeEquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.LoadPlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase

class GetEquipmentLevelUpgradeOptionsUseCaseTest {

    @Test
    fun `returns all options up to item maximum level`() = runBlocking {
        val weapon = createWeapon(level = 1)
        val options = createUseCase(
            playerRepository = FakePlayerRepository(
                initialState = createPlayerState(
                    silverAmount = 100,
                    items = listOf(weapon)
                )
            )
        )(weapon.id)

        assertEquals(19, options.options.size)
        assertEquals(20, options.options.last().targetLevel)

        val threeLevelOption = options.options.single { it.levelsToUpgrade == 3 }

        assertEquals(4, threeLevelOption.targetLevel)
        assertEquals(90, threeLevelOption.totalCost)
        assertEquals(emptyList<Int>(), threeLevelOption.randomUpgradeLevels)
        assertEquals(
            14.5f,
            threeLevelOption.guaranteedStatChanges.single().updatedValue,
            FLOAT_EPSILON
        )
    }

    @Test
    fun `includes fifth level as random upgrade in option`() = runBlocking {
        val weapon = createWeapon(level = 1)
        val options = createUseCase(
            playerRepository = FakePlayerRepository(
                initialState = createPlayerState(
                    silverAmount = 200,
                    items = listOf(weapon)
                )
            )
        )(weapon.id)

        val fifthLevelOption = options.options.single { it.targetLevel == 5 }

        assertEquals(4, fifthLevelOption.levelsToUpgrade)
        assertEquals(5, fifthLevelOption.targetLevel)
        assertEquals(140, fifthLevelOption.totalCost)
        assertEquals(listOf(5), fifthLevelOption.randomUpgradeLevels)
        assertEquals(
            16f,
            fifthLevelOption.guaranteedStatChanges.single().updatedValue,
            FLOAT_EPSILON
        )
    }

    private fun createUseCase(
        playerRepository: FakePlayerRepository
    ): GetEquipmentLevelUpgradeOptionsUseCase {
        return GetEquipmentLevelUpgradeOptionsUseCase(
            getOrCreatePlayerStateUseCase = GetOrCreatePlayerStateUseCase(
                loadPlayerStateUseCase = LoadPlayerStateUseCase(
                    playerRepository = playerRepository
                ),
                savePlayerStateUseCase = SavePlayerStateUseCase(
                    playerRepository = playerRepository
                )
            ),
            equipmentDefinitionRepository = FakeEquipmentDefinitionRepository(
                initialDefinitions = listOf(weaponDefinition)
            ),
            equipmentLevelUpgradeCostCalculator = EquipmentLevelUpgradeCostCalculator(),
            equipmentLevelUpgradeStatCalculator = EquipmentLevelUpgradeStatCalculator(
                equipmentStatConfigRepository = FakeEquipmentStatConfigRepository()
            )
        )
    }
}