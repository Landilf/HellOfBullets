package ru.landilf.hellofbullets.domain.usecase

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord

class SyncSurvivalLeaderboardUseCaseTest {

    @Test
    fun `replaces online player id with local id in cached leaderboard`() = runBlocking {
        val playerRepository = FakePlayerRepository()
        val leaderboardRepository = FakeLeaderboardRepository(
            initialRecords = listOf(
                LeaderboardRecord(
                    id = LOCAL_PLAYER_RECORD_ID,
                    playerName = "Player",
                    time = 25
                )
            )
        )
        val onlineLeaderboardRepository = FakeOnlineLeaderboardRepository(
            initialRecords = createOnlineRivalRecords()
        )
        val useCase = createUseCase(
            playerRepository = playerRepository,
            leaderboardRepository = leaderboardRepository,
            onlineLeaderboardRepository = onlineLeaderboardRepository
        )

        useCase()

        assertEquals(
            25,
            leaderboardRepository
                .getRecordById(LOCAL_PLAYER_RECORD_ID)
                ?.time
        )
        assertNull(leaderboardRepository.getRecordById(ONLINE_PLAYER_RECORD_ID))
        assertEquals(
            25,
            onlineLeaderboardRepository.lastSubmittedRecord?.time
        )
        assertEquals(20, leaderboardRepository.getAllRecords().size)
    }

    @Test
    fun `keeps local player record when it is outside online top`() = runBlocking {
        val playerRepository = FakePlayerRepository()
        val leaderboardRepository = FakeLeaderboardRepository(
            initialRecords = listOf(
                LeaderboardRecord(
                    id = LOCAL_PLAYER_RECORD_ID,
                    playerName = "Player",
                    time = 1
                )
            )
        )
        val onlineLeaderboardRepository = FakeOnlineLeaderboardRepository(
            initialRecords = createOnlineRivalRecords()
        )
        val useCase = createUseCase(
            playerRepository = playerRepository,
            leaderboardRepository = leaderboardRepository,
            onlineLeaderboardRepository = onlineLeaderboardRepository
        )

        useCase()

        assertEquals(
            1,
            leaderboardRepository
                .getRecordById(LOCAL_PLAYER_RECORD_ID)
                ?.time
        )
        assertEquals(21, leaderboardRepository.getAllRecords().size)
        assertTrue(
            leaderboardRepository.getLeaderboard().none { record ->
                record.id == LOCAL_PLAYER_RECORD_ID
            }
        )
    }

    private fun createUseCase(
        playerRepository: FakePlayerRepository,
        leaderboardRepository: FakeLeaderboardRepository,
        onlineLeaderboardRepository: FakeOnlineLeaderboardRepository
    ): SyncSurvivalLeaderboardUseCase {
        val savePlayerStateUseCase = SavePlayerStateUseCase(playerRepository)

        return SyncSurvivalLeaderboardUseCase(
            getOrCreatePlayerStateUseCase = GetOrCreatePlayerStateUseCase(
                loadPlayerStateUseCase = LoadPlayerStateUseCase(playerRepository),
                savePlayerStateUseCase = savePlayerStateUseCase
            ),
            leaderboardRepository = leaderboardRepository,
            onlineLeaderboardRepository = onlineLeaderboardRepository
        )
    }

    private fun createOnlineRivalRecords(): List<LeaderboardRecord> {
        return List(20) { index ->
            LeaderboardRecord(
                id = "online-rival-${index + 1}",
                playerName = "Rival ${index + 1}",
                time = (20 - index) * 10
            )
        }
    }

    private companion object {
        const val LOCAL_PLAYER_RECORD_ID = "1"
        const val ONLINE_PLAYER_RECORD_ID = "online-player"
    }
}