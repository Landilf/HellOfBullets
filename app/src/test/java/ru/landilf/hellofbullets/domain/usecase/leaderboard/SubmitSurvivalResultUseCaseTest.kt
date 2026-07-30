package ru.landilf.hellofbullets.domain.usecase.leaderboard

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.landilf.hellofbullets.domain.engine.player.PlayerProgressionCalculator
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.domain.usecase.FakeLeaderboardRepository
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository
import ru.landilf.hellofbullets.domain.usecase.player.ApplyPlayerRewardUseCase
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.LoadPlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.survival.CalculateSurvivalRewardUseCase

class SubmitSurvivalResultUseCaseTest {
    @Test
    fun `creates player saves reward and adds result to leaderboard`() = runBlocking {
        val playerRepository = FakePlayerRepository()
        val leaderboardRepository = FakeLeaderboardRepository()
        val useCase = createUseCase(
            playerRepository = playerRepository,
            leaderboardRepository = leaderboardRepository
        )

        val result = useCase(time = 25)

        val playerState = requireNotNull(playerRepository.state)
        val playerRecord = leaderboardRepository.getRecordById("1")

        assertTrue(result.isNewRecord)
        assertEquals(1, result.leaderboardPosition)
        assertEquals(25, result.leaderboardCutoffTime)
        assertEquals(2, playerState.playerProfile.totalExperience)
        assertEquals(1, playerState.playerProfile.silverAmount)
        assertEquals(25, playerRecord?.time)
    }

    @Test
    fun `keeps best player record and still grants reward for weaker attempt`() = runBlocking {
        val playerRepository = FakePlayerRepository()
        val leaderboardRepository = FakeLeaderboardRepository()
        val useCase = createUseCase(
            playerRepository = playerRepository,
            leaderboardRepository = leaderboardRepository
        )

        useCase(time = 50)
        val result = useCase(time = 20)

        val playerState = requireNotNull(playerRepository.state)
        val playerRecord = leaderboardRepository.getRecordById("1")

        assertFalse(result.isNewRecord)
        assertEquals(50, playerRecord?.time)
        assertEquals(7, playerState.playerProfile.totalExperience)
        assertEquals(4, playerState.playerProfile.silverAmount)
    }

    @Test
    fun `returns leaderboard cutoff when player result does not enter top`() = runBlocking {
        val playerRepository = FakePlayerRepository()
        val leaderboardRepository = FakeLeaderboardRepository(
            initialRecords = List(20) { index ->
                LeaderboardRecord(
                    id = "rival-$index",
                    playerName = "Rival $index",
                    time = 29 - index
                )
            }
        )
        val useCase = createUseCase(
            playerRepository = playerRepository,
            leaderboardRepository = leaderboardRepository
        )

        val result = useCase(time = 1)

        assertTrue(result.isNewRecord)
        assertNull(result.leaderboardPosition)
        assertEquals(10, result.leaderboardCutoffTime)
    }

    private fun createUseCase(
        playerRepository: FakePlayerRepository,
        leaderboardRepository: FakeLeaderboardRepository
    ): SubmitSurvivalResultUseCase {
        val savePlayerStateUseCase = SavePlayerStateUseCase(playerRepository)

        return SubmitSurvivalResultUseCase(
            getOrCreatePlayerStateUseCase = GetOrCreatePlayerStateUseCase(
                loadPlayerStateUseCase = LoadPlayerStateUseCase(playerRepository),
                savePlayerStateUseCase = savePlayerStateUseCase
            ),
            leaderboardRepository = leaderboardRepository,
            calculateSurvivalRewardUseCase = CalculateSurvivalRewardUseCase(),
            applyPlayerRewardUseCase = ApplyPlayerRewardUseCase(
                playerProgressionCalculator = PlayerProgressionCalculator(),
                savePlayerStateUseCase = savePlayerStateUseCase
            )
        )
    }
}