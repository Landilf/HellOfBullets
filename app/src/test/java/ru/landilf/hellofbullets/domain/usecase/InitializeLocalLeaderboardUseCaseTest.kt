package ru.landilf.hellofbullets.domain.usecase

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord

class InitializeLocalLeaderboardUseCaseTest {
    @Test
    fun `adds all mock rivals even if player record exists`() = runBlocking {
        val leaderboardRepository = FakeLeaderboardRepository(
            initialRecords = listOf(
                LeaderboardRecord(
                    id = "1",
                    playerName = "Player",
                    time = 7
                )
            )
        )
        val useCase = InitializeLocalLeaderboardUseCase(
            leaderboardRepository = leaderboardRepository
        )

        useCase()

        val records = leaderboardRepository.getAllRecords()

        assertEquals(21, records.size)
        assertTrue(
            (1..20).all { index ->
                records.any { record ->
                    record.playerName == "Rival $index"
                }
            }
        )
        assertTrue(
            records.any { record ->
                record.playerName == "Player" && record.time == 7
            }
        )
    }

    @Test
    fun `does not write mock rivals on repeated initialization`() = runBlocking {
        val leaderboardRepository = FakeLeaderboardRepository()
        val useCase = InitializeLocalLeaderboardUseCase(
            leaderboardRepository = leaderboardRepository
        )

        useCase()
        val upsertCallCountAfterFirstInitialization = leaderboardRepository.upsertCallCount

        useCase()

        assertEquals(
            upsertCallCountAfterFirstInitialization,
            leaderboardRepository.upsertCallCount
        )
        assertEquals(
            20,
            leaderboardRepository.getAllRecords().size
        )
    }
}