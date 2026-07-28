package ru.landilf.hellofbullets.domain.usecase

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord

class UpdatePlayerNameUseCaseTest {

    @Test
    fun `update player profile and local leaderboard record`() = runBlocking {
        val playerRepository = FakePlayerRepository()
        val leaderboardRepository = FakeLeaderboardRepository(
            initialRecords = listOf(
                LeaderboardRecord(
                    id = PLAYER_RECORD_ID,
                    playerName = "Player",
                    time = 42
                )
            )
        )
        val useCase = createUseCase(
            playerRepository = playerRepository,
            leaderboardRepository = leaderboardRepository
        )

        useCase("   New Player  ")

        assertEquals(
            "New Player",
            playerRepository.state?.playerProfile?.name
        )
        assertEquals(
            "New Player",
            leaderboardRepository
                .getRecordById(PLAYER_RECORD_ID)
                ?.playerName
        )
    }

    @Test
    fun `does not create leaderboard record when player has no record`() = runBlocking {
        val playerRepository = FakePlayerRepository()
        val leaderboardRepository = FakeLeaderboardRepository()
        val useCase = createUseCase(
            playerRepository = playerRepository,
            leaderboardRepository = leaderboardRepository
        )

        useCase("New Player")

        assertEquals(
            "New Player",
            playerRepository.state?.playerProfile?.name
        )
        assertNull(leaderboardRepository.getRecordById(PLAYER_RECORD_ID))
    }

    @Test
    fun `rejects blank player name`() = runBlocking {
        val useCase = createUseCase(
            playerRepository = FakePlayerRepository(),
            leaderboardRepository = FakeLeaderboardRepository()
        )

        try {
            useCase("   ")
            fail("Expected IllegalArgumentException")
        } catch (exception: IllegalArgumentException) {
            assertEquals(
                "Имя не может быть пустым",
                exception.message
            )
        }
    }

    @Test
    fun `rejects player name longer than maximum length`() = runBlocking {
        val useCase = createUseCase(
            playerRepository = FakePlayerRepository(),
            leaderboardRepository = FakeLeaderboardRepository()
        )

        try {
            useCase("a".repeat(33))
            fail("Expected IllegalArgumentException")
        } catch (exception: IllegalArgumentException) {
            assertEquals(
                "Имя должно содержать не более 32 символов",
                exception.message
            )
        }
    }

    private fun createUseCase(
        playerRepository: FakePlayerRepository,
        leaderboardRepository: FakeLeaderboardRepository
    ): UpdatePlayerNameUseCase {
        val savePlayerStateUseCase = SavePlayerStateUseCase(playerRepository)

        return UpdatePlayerNameUseCase(
            getOrCreatePlayerStateUseCase = GetOrCreatePlayerStateUseCase(
                loadPlayerStateUseCase = LoadPlayerStateUseCase(playerRepository),
                savePlayerStateUseCase = savePlayerStateUseCase
            ),
            savePlayerStateUseCase = savePlayerStateUseCase,
            leaderboardRepository = leaderboardRepository
        )
    }

    private companion object {
        const val PLAYER_RECORD_ID = "1"
    }
}
