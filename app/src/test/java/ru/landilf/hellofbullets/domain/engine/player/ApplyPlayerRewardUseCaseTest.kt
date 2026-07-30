package ru.landilf.hellofbullets.domain.engine.player

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.battle.common.result.RewardInfo
import ru.landilf.hellofbullets.domain.model.player.Inventory
import ru.landilf.hellofbullets.domain.model.player.PlayerBuild
import ru.landilf.hellofbullets.domain.model.player.PlayerProfile
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository
import ru.landilf.hellofbullets.domain.usecase.player.ApplyPlayerRewardUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase

class ApplyPlayerRewardUseCaseTest {
    @Test
    fun `adds reward without increasing level`() = runBlocking {
        val playerRepository = FakePlayerRepository()
        val useCase = createUseCase(playerRepository)
        val playerState = createPlayerState(
            level = 1,
            totalExperience = 10,
            silverAmount = 5,
            skillPointAmount = 0
        )

        val updatedState = useCase(
            playerState = playerState,
            reward = RewardInfo(
                exp = 10,
                silver = 3
            )
        )

        assertEquals(1, updatedState.playerProfile.level)
        assertEquals(20, updatedState.playerProfile.totalExperience)
        assertEquals(8, updatedState.playerProfile.silverAmount)
        assertEquals(0, updatedState.playerProfile.skillPointAmount)
    }

    @Test
    fun `increases level and grants skill point`() = runBlocking {
        val playerRepository = FakePlayerRepository()
        val useCase = createUseCase(playerRepository)
        val playerState = createPlayerState(
            level = 1,
            totalExperience = 24,
            silverAmount = 0,
            skillPointAmount = 0
        )

        val updatedState = useCase(
            playerState = playerState,
            reward = RewardInfo(
                exp = 1,
                silver = 2
            )
        )

        assertEquals(2, updatedState.playerProfile.level)
        assertEquals(25, updatedState.playerProfile.totalExperience)
        assertEquals(2, updatedState.playerProfile.silverAmount)
        assertEquals(1, updatedState.playerProfile.skillPointAmount)
    }

    @Test
    fun `grants skill points for every gained level`() = runBlocking {
        val playerRepository = FakePlayerRepository()
        val useCase = createUseCase(playerRepository)
        val playerState = createPlayerState(
            level = 1,
            totalExperience = 0,
            silverAmount = 0,
            skillPointAmount = 2
        )

        val updatedState = useCase(
            playerState = playerState,
            reward = RewardInfo(
                exp = 150,
                silver = 10
            )
        )

        assertEquals(4, updatedState.playerProfile.level)
        assertEquals(150, updatedState.playerProfile.totalExperience)
        assertEquals(5, updatedState.playerProfile.skillPointAmount)
    }

    private fun createUseCase(
        playerRepository: FakePlayerRepository
    ): ApplyPlayerRewardUseCase {
        return ApplyPlayerRewardUseCase(
            playerProgressionCalculator = PlayerProgressionCalculator(),
            savePlayerStateUseCase = SavePlayerStateUseCase(playerRepository)
        )
    }

    private fun createPlayerState(
        level: Int,
        totalExperience: Int,
        silverAmount: Int,
        skillPointAmount: Int
    ): PlayerState {
        return PlayerState(
            playerProfile = PlayerProfile(
                id = 1L,
                name = "Player",
                level = level,
                totalExperience = totalExperience,
                silverAmount = silverAmount,
                skillPointAmount = skillPointAmount
            ),
            playerBuild = PlayerBuild(
                equippedWeaponItem = null,
                equippedArmorItem = null,
                equippedArtifactItem = null,
                firstSkillSlot = null,
                secondSkillSlot = null
            ),
            inventory = Inventory(ownedItems = emptyList())
        )
    }
}