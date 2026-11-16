package ru.landilf.hellofbullets.domain.usecase.equipment

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.createWeapon
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.weaponDefinition
import ru.landilf.hellofbullets.fixtures.FLOAT_EPSILON
import ru.landilf.hellofbullets.domain.fixtures.PlayerTestFixtures.createPlayerState
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQualityUpgradeOptions
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.usecase.FakeEquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.LoadPlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase

class GetEquipmentQualityUpgradeOptionsUseCaseTest {
    @Test
    fun `returns only matching items as quality upgrade materials`() = runBlocking {
        val targetWeapon = createWeapon(id = 1L)
        val matchingMaterials = (2L..8L).map(::createWeapon)
        val equippedMaterial = matchingMaterials.first()
        val eligibleMaterials = matchingMaterials.drop(1)

        val differentQualityItem = createWeapon(
            id = 8L,
            quality = EquipmentQuality.FINE
        )
        val differentDefinitionItem = createWeapon(
            id = 9L,
            definitionId = 999L
        )

        val useCase = createUseCase(
            playerRepository = FakePlayerRepository(
                initialState = createPlayerState(
                    equippedWeapon = equippedMaterial,
                    items = listOf(
                        targetWeapon,
                        *matchingMaterials.toTypedArray(),
                        differentQualityItem,
                        differentDefinitionItem
                    )
                )
            )
        )

        val result = useCase(targetWeapon.id)
                as EquipmentQualityUpgradeOptions.Available

        assertEquals(targetWeapon.id, result.itemId)
        assertEquals(EquipmentQuality.NORMAL, result.currentQuality)
        assertEquals(EquipmentQuality.FINE, result.nextQuality)
        assertEquals(
            EquipmentStatType.DAMAGE,
            result.primaryFirstStatPreview.statType
        )
        assertEquals(
            10f,
            result.primaryFirstStatPreview.currentValue,
            FLOAT_EPSILON
        )
        assertEquals(
            11f,
            result.primaryFirstStatPreview.upgradedValue,
            FLOAT_EPSILON
        )
        assertEquals(6, result.requiredMaterialsCount)
        assertEquals(
            eligibleMaterials.map { it.id },
            result.eligibleMaterialItemIds
        )
    }

    @Test
    fun `returns unavailable for legendary item`() = runBlocking {
        val legendaryWeapon = createWeapon(
            quality = EquipmentQuality.LEGENDARY
        )
        val useCase = createUseCase(
            playerRepository = FakePlayerRepository(
                initialState = createPlayerState(
                    items = listOf(legendaryWeapon)
                )
            )
        )

        val result = useCase(legendaryWeapon.id)

        assertEquals(
            EquipmentQualityUpgradeOptions.Unavailable(
                currentQuality = EquipmentQuality.LEGENDARY
            ),
            result
        )
    }

    private fun createUseCase(
        playerRepository: FakePlayerRepository
    ): GetEquipmentQualityUpgradeOptionsUseCase {
        return GetEquipmentQualityUpgradeOptionsUseCase(
            getOrCreatePlayerStateUseCase = GetOrCreatePlayerStateUseCase(
                loadPlayerStateUseCase = LoadPlayerStateUseCase(playerRepository),
                savePlayerStateUseCase = SavePlayerStateUseCase(playerRepository)
            ),
            equipmentDefinitionRepository = FakeEquipmentDefinitionRepository(
                initialDefinitions = listOf(weaponDefinition)
            )
        )
    }

}