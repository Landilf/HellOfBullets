package ru.landilf.hellofbullets.domain.usecase.equipment

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.createWeapon
import ru.landilf.hellofbullets.domain.fixtures.PlayerTestFixtures.createPlayerState
import ru.landilf.hellofbullets.domain.model.player.EquipmentSlot
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.LoadPlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase

class SetEquippedItemUseCaseTest {
    @Test
    fun `equips owned item and saves player state`() = runBlocking {
        val weapon = createWeapon()
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                equippedWeapon = null,
                items = listOf(weapon)
            )
        )
        val useCase = createUseCase(playerRepository)

        val updatedState = useCase(
            slot = EquipmentSlot.WEAPON,
            itemId = weapon.id
        )

        assertEquals(
            weapon,
            updatedState.playerBuild.equippedWeaponItem
        )
        assertEquals(updatedState, playerRepository.state)
    }

    @Test
    fun `clears equipment slot and saves player state`() = runBlocking {
        val weapon = createWeapon()
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                equippedWeapon = weapon,
                items = listOf(weapon)
            )
        )
        val useCase = createUseCase(playerRepository)

        val updatedState = useCase(
            slot = EquipmentSlot.WEAPON,
            itemId = null
        )

        assertEquals(
            null,
            updatedState.playerBuild.equippedWeaponItem
        )
        assertEquals(updatedState, playerRepository.state)
    }

    private fun createUseCase(
        playerRepository: FakePlayerRepository
    ): SetEquippedItemUseCase {
        val savePlayerStateUseCase = SavePlayerStateUseCase(playerRepository)

        return SetEquippedItemUseCase(
            getOrCreatePlayerStateUseCase = GetOrCreatePlayerStateUseCase(
                loadPlayerStateUseCase = LoadPlayerStateUseCase(playerRepository),
                savePlayerStateUseCase = savePlayerStateUseCase
            ),
            savePlayerStateUseCase = savePlayerStateUseCase
        )
    }
}