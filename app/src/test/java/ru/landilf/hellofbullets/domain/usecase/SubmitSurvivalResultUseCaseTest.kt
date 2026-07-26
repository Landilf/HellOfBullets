package ru.landilf.hellofbullets.domain.usecase

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

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
        assertEquals(19, result.leaderboardPosition)
        assertEquals(20, result.leaderboardCutoffTime)
        assertEquals(2, playerState.playerProfile.expAmount)
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
        assertEquals(7, playerState.playerProfile.expAmount)
        assertEquals(4, playerState.playerProfile.silverAmount)
    }

    @Test
    fun `returns leaderboard cutoff when player result does not enter top`() = runBlocking {
        val playerRepository = FakePlayerRepository()
        val leaderboardRepository = FakeLeaderboardRepository()
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
            savePlayerStateUseCase = savePlayerStateUseCase,
            initializeLocalLeaderboardUseCase = InitializeLocalLeaderboardUseCase(
                leaderboardRepository = leaderboardRepository
            )
        )
    }
}