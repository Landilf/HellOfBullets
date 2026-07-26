package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository
import ru.landilf.hellofbullets.domain.repository.PlayerRepository

class FakeLeaderboardRepository(
    initialRecords: List<LeaderboardRecord> = emptyList()
) : LeaderboardRepository {
    private val recordsById = initialRecords
        .associateBy { it.id }
        .toMutableMap()

    var upsertCallCount = 0
        private set

    override suspend fun getLeaderboard(): List<LeaderboardRecord> {
        return recordsById.values
            .sortedByDescending { it.time }
            .take(MAX_LEADERBOARD_SIZE)
    }

    override suspend fun getRecordById(
        id: String
    ): LeaderboardRecord? {
        return recordsById[id]
    }

    override suspend fun getExistingRecordIds(
        ids: List<String>
    ): Set<String> {
        return recordsById.keys.intersect(ids.toSet())
    }

    override suspend fun upsertRecord(
        record: LeaderboardRecord
    ) {
        recordsById[record.id] = record
        upsertCallCount++
    }

    override suspend fun clearLeaderboard() {
        recordsById.clear()
    }

    fun getAllRecords(): List<LeaderboardRecord> {
        return recordsById.values.toList()
    }

    private companion object {
        const val MAX_LEADERBOARD_SIZE = 20
    }
}

class FakePlayerRepository(
    initialState: PlayerState? = null
) : PlayerRepository {
    var state: PlayerState? = initialState
        private set

    override suspend fun getPlayerState(): PlayerState? {
        return state
    }

    override suspend fun savePlayerState(state: PlayerState) {
        this.state = state
    }

    override suspend fun clearPlayerState() {
        state = null
    }


}