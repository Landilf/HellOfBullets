package ru.landilf.hellofbullets.domain.usecase.equipment

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.equipment.definition.StatRange
import ru.landilf.hellofbullets.domain.model.equipment.definition.WeaponDefinition
import ru.landilf.hellofbullets.domain.model.player.Inventory
import ru.landilf.hellofbullets.domain.model.player.PlayerBuild
import ru.landilf.hellofbullets.domain.model.player.PlayerProfile
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.usecase.FakeEquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.LoadPlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase

class UpgradePlayerEquipmentQualityUseCaseTest {
    @Test
    fun `upgrades quality consumes meterials and updates player state`() = runBlocking {
        val targetWeapon = createWeapon(id = 1L)
        val materials = (2L..7L).map(::createWeapon)
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                equippedWeapon = materials.first(),
                items = listOf(targetWeapon) + materials
            )
        )
        val useCase = createUseCase(playerRepository)

        val result = useCase(
            itemId = targetWeapon.id,
            materialItemIds = materials.map { it.id }
        )
        val upgradedWeapon = result.upgradedItem as WeaponItem

        assertEquals(EquipmentQuality.FINE, upgradedWeapon.quality)
        assertEquals(47.5f, upgradedWeapon.damage, EPSILON)
        assertEquals(materials.map { it.id }, result.consumedMaterialIds)
        assertEquals(
            listOf(upgradedWeapon),
            playerRepository.state?.inventory?.ownedItems
        )
        assertNull(playerRepository.state?.playerBuild?.equippedWeaponItem)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when selected material is absent from inventory`(): Unit = runBlocking {
        val targetWeapon = createWeapon(id = 1L)
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                equippedWeapon = targetWeapon,
                items = listOf(targetWeapon)
            )
        )
        val useCase = createUseCase(playerRepository)

        useCase(
            itemId = targetWeapon.id,
            materialItemIds = listOf(999L)
        )
    }

    private fun createUseCase(
        playerRepository: FakePlayerRepository
    ): UpgradePlayerEquipmentQualityUseCase {
        val savePlayerStateUseCase = SavePlayerStateUseCase(playerRepository)

        return UpgradePlayerEquipmentQualityUseCase(
            getOrCreatePlayerStateUseCase = GetOrCreatePlayerStateUseCase(
                loadPlayerStateUseCase = LoadPlayerStateUseCase(playerRepository),
                savePlayerStateUseCase = savePlayerStateUseCase
            ),
            savePlayerStateUseCase = savePlayerStateUseCase,
            equipmentDefinitionRepository = FakeEquipmentDefinitionRepository(
                initialDefinitions = listOf(weaponDefinition)
            ),
            upgradeEquipmentQualityUseCase = UpgradeEquipmentQualityUseCase()
        )
    }

    private fun createPlayerState(
        equippedWeapon: WeaponItem?,
        items: List<WeaponItem>
    ): PlayerState {
        return PlayerState(
            playerProfile = PlayerProfile(
                id = 1L,
                name = "Player",
                level = 1,
                totalExperience = 0,
                silverAmount = 0,
                skillPointAmount = 0
            ),
            playerBuild = PlayerBuild(
                equippedWeaponItem = equippedWeapon,
                equippedArmorItem = null,
                equippedArtifactItem = null,
                firstSkillSlot = null,
                secondSkillSlot = null
            ),
            inventory = Inventory(ownedItems = items)
        )
    }

    private fun createWeapon(
        id: Long,
        specializationCoef: Float = 0f
    ): WeaponItem {
        return WeaponItem(
            id = id,
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
            basePurchasePrice = 100,
            baseLevelUpgradeCost = 10,
            damageRange = StatRange(9f, 11f),
            attackSpeedRange = StatRange(1.8f, 2.2f),
            attackRange = 500f
        )

        const val EPSILON = 0.0001f
    }
}