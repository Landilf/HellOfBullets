package ru.landilf.hellofbullets.domain.usecase.equipment

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
import ru.landilf.hellofbullets.domain.model.equipment.definition.WeaponDefinition
import ru.landilf.hellofbullets.domain.usecase.FakeEquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.LoadPlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase

class UpgradePlayerEquipmentLevelUseCaseTest {
    @Test
    fun `upgrades item and saves updated player state`() = runBlocking {
        val weapon = createWeapon()
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                silverAmount = 100,
                equippedWeapon = weapon,
                items = listOf(weapon)
            )
        )
        val useCase = createUseCase(
            playerRepository = playerRepository,
            definitions = listOf(weaponDefinition)
        )

        val updatedItem = useCase(itemId = weapon.id) as WeaponItem

        assertEquals(
            80,
            playerRepository.state?.playerProfile?.silverAmount
        )
        assertEquals(2, updatedItem.level)
        assertEquals(11.5f, updatedItem.damage, FLOAT_EPSILON)
        assertEquals(
            updatedItem,
            playerRepository.state
                ?.inventory
                ?.ownedItems
                ?.single()
        )
        assertEquals(
            updatedItem,
            playerRepository.state?.playerBuild?.equippedWeaponItem
        )
    }

    @Test
    fun `upgrades additional stat selected randomly on fifth level`() = runBlocking {
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
            definitions = listOf(weaponDefinition),
            equipmentRandomGenerator = FakeEquipmentRandomGenerator(
                nextIntValue = 1
            )
        )

        val updatedItem = useCase(weapon.id) as WeaponItem

        assertEquals(5, updatedItem.level)
        assertEquals(11.5f, updatedItem.damage, FLOAT_EPSILON)
        assertEquals(5f, updatedItem.attackSpeed, FLOAT_EPSILON)
        assertEquals(3.3f, updatedItem.additionalStatValue, FLOAT_EPSILON)
        assertEquals(50, playerRepository.state?.playerProfile?.silverAmount)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when item is absent from player inventory`(): Unit = runBlocking {
        val weapon = createWeapon()
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                equippedWeapon = createWeapon(),
                items = listOf(weapon)
            )
        )
        val useCase = createUseCase(
            playerRepository = playerRepository,
            definitions = listOf(weaponDefinition)
        )

        useCase(itemId = 999L)
    }

    @Test(expected = IllegalStateException::class)
    fun `throws when item definition is absent from catalog`(): Unit = runBlocking {
        val weapon = createWeapon()
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                equippedWeapon = weapon,
                items = listOf(weapon)
            )
        )
        val useCase = createUseCase(
            playerRepository = playerRepository,
            definitions = emptyList()
        )

        useCase(itemId = weapon.id)
    }

    @Test
    fun `throws when player does not have enough silver`(): Unit = runBlocking {
        val weapon = createWeapon()
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                silverAmount = 19,
                equippedWeapon = weapon,
                items = listOf(weapon)
            )
        )
        val useCase = createUseCase(
            playerRepository = playerRepository,
            definitions = listOf(weaponDefinition)
        )

        val initialState = playerRepository.state

        val exception = runCatching {
            useCase(itemId = weapon.id)
        }.exceptionOrNull()

        assertTrue(exception is IllegalArgumentException)
        assertEquals(initialState, playerRepository.state)
    }

    private fun createUseCase(
        playerRepository: FakePlayerRepository,
        definitions: List<WeaponDefinition>,
        equipmentRandomGenerator: EquipmentRandomGenerator = FakeEquipmentRandomGenerator()
    ): UpgradePlayerEquipmentLevelUseCase {
        val savePlayerStateUseCase = SavePlayerStateUseCase(playerRepository)

        return UpgradePlayerEquipmentLevelUseCase(
            getOrCreatePlayerStateUseCase = GetOrCreatePlayerStateUseCase(
                loadPlayerStateUseCase = LoadPlayerStateUseCase(playerRepository),
                savePlayerStateUseCase = savePlayerStateUseCase
            ),
            savePlayerStateUseCase = savePlayerStateUseCase,
            equipmentDefinitionRepository = FakeEquipmentDefinitionRepository(
                initialDefinitions = definitions
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
        override fun nextFloat(
            from: Float,
            until: Float
        ): Float {
            error("nextFloat не должен вызываться в тестах повышения уровня")
        }

        override fun nextInt(until: Int): Int {
            return nextIntValue
        }
    }
}