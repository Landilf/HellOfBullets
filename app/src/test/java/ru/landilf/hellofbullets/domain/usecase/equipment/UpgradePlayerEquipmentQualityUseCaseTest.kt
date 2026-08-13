package ru.landilf.hellofbullets.domain.usecase.equipment

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.createWeapon
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.weaponDefinition
import ru.landilf.hellofbullets.domain.fixtures.FLOAT_EPSILON
import ru.landilf.hellofbullets.domain.fixtures.PlayerTestFixtures.createPlayerState
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.usecase.FakeEquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.LoadPlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase

class UpgradePlayerEquipmentQualityUseCaseTest {
    @Test
    fun `upgrades quality consumes materials and updates player state`() = runBlocking {
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
        assertEquals(11f, upgradedWeapon.damage, FLOAT_EPSILON)
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
}