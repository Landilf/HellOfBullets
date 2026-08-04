package ru.landilf.hellofbullets.domain.usecase.equipment

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.landilf.hellofbullets.domain.engine.equipment.EquipmentLevelUpgradeCostCalculator
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.equipment.definition.AdditionalStatConfig
import ru.landilf.hellofbullets.domain.model.equipment.definition.StatRange
import ru.landilf.hellofbullets.domain.model.equipment.definition.WeaponDefinition
import ru.landilf.hellofbullets.domain.model.player.Inventory
import ru.landilf.hellofbullets.domain.model.player.PlayerBuild
import ru.landilf.hellofbullets.domain.model.player.PlayerProfile
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.repository.EquipmentStatConfigRepository
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
            initialState = createPlayerState(weapon)
        )
        val useCase = createUseCase(
            playerRepository = playerRepository,
            definitions = listOf(weaponDefinition)
        )

        val updatedItem = useCase(
            itemId = weapon.id,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.PRIMARY_SECOND
        ) as WeaponItem

        assertEquals(
            80,
            playerRepository.state?.playerProfile?.silverAmount
        )
        assertEquals(2, updatedItem.level)
        assertEquals(11.5f, updatedItem.damage, EPSILON)
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

    @Test(expected = IllegalArgumentException::class)
    fun `throws when item is absent from player inventory`(): Unit = runBlocking {
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(createWeapon())
        )
        val useCase = createUseCase(
            playerRepository = playerRepository,
            definitions = listOf(weaponDefinition)
        )

        useCase(
            itemId = 999L,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.PRIMARY_SECOND
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `throws when item definition is absent from catalog`(): Unit = runBlocking {
        val weapon = createWeapon()
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(weapon)
        )
        val useCase = createUseCase(
            playerRepository = playerRepository,
            definitions = emptyList()
        )

        useCase(
            itemId = weapon.id,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.PRIMARY_SECOND
        )
    }

    @Test
    fun `throws when player does not have enough silver`(): Unit = runBlocking {
        val weapon = createWeapon()
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(weapon).copy(
                playerProfile = PlayerProfile(
                    id = 1L,
                    name = "Player",
                    level = 1,
                    totalExperience = 0,
                    silverAmount = 19,
                    skillPointAmount = 0
                )
            )
        )
        val useCase = createUseCase(
            playerRepository = playerRepository,
            definitions = listOf(weaponDefinition)
        )

        val initialState = playerRepository.state

        val exception = runCatching {
            useCase(
                itemId = weapon.id,
                fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.PRIMARY_SECOND
            )
        }.exceptionOrNull()

        assertTrue(exception is IllegalArgumentException)
        assertEquals(initialState, playerRepository.state)

    }

    private fun createUseCase(
        playerRepository: FakePlayerRepository,
        definitions: List<WeaponDefinition>
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
                equipmentStatConfigRepository = object : EquipmentStatConfigRepository {
                    override fun getReferenceRange(statType: EquipmentStatType): StatRange {
                        error("Диапазон характеристики не должен использоваться в этом тесте")
                    }

                    override fun getAdditionalStatConfig(statType: EquipmentStatType): AdditionalStatConfig {
                        error("Конфиг дополнительной характеристики не должен использоваться в этом тесте")
                    }
                }
            )
        )
    }

    private fun createPlayerState(
        weapon: WeaponItem
    ): PlayerState {
        return PlayerState(
            playerProfile = PlayerProfile(
                id = 1L,
                name = "Player",
                level = 1,
                totalExperience = 0,
                silverAmount = 100,
                skillPointAmount = 0
            ),
            playerBuild = PlayerBuild(
                equippedWeaponItem = weapon,
                equippedArmorItem = null,
                equippedArtifactItem = null,
                firstSkillSlot = null,
                secondSkillSlot = null
            ),
            inventory = Inventory(listOf(weapon))
        )
    }

    private fun createWeapon(
        specializationCoef: Float = 0f
    ): WeaponItem {
        return WeaponItem(
            id = 1L,
            definitionId = weaponDefinition.id,
            level = 1,
            quality = EquipmentQuality.NORMAL,
            additionalStatType = EquipmentStatType.HP,
            additionalStatValue = 0f,
            damage = 10f,
            attackSpeed = 2f,
            specializationCoef = specializationCoef
        )
    }

    private companion object {
        val weaponDefinition = WeaponDefinition(
            id = 1L,
            name = "Pistol",
            primaryFirstGrowthMultiplier = 1.5f,
            primarySecondGrowthMultiplier = 0.25f,
            damageRange = StatRange(9f, 11f),
            attackSpeedRange = StatRange(1.8f, 2.2f),
            attackRange = 500f,
            baseLevelUpgradeCost = 10
        )

        const val EPSILON = 0.0001f
    }
}