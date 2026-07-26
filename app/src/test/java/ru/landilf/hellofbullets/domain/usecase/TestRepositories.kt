package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository
import ru.landilf.hellofbullets.domain.repository.OnlineLeaderboardRepository
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

    override suspend fun replaceLeaderboard(
        records: List<LeaderboardRecord>
    ) {
        recordsById.clear()

        for (record in records) {
            recordsById[record.id] = record
        }
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

class FakeOnlineLeaderboardRepository(
    private val playerId: String = "online-player",
    initialRecords: List<LeaderboardRecord> = emptyList()
) : OnlineLeaderboardRepository {
    private val recordsById = initialRecords
        .associateBy { it.id }
        .toMutableMap()

    var lastSubmittedRecord: LeaderboardRecord? = null
        private set

    override suspend fun getOrCreatePlayerId(): String {
        return playerId
    }

    override suspend fun getTopSurvivalRecords(
        limit: Int
    ): List<LeaderboardRecord> {
        return recordsById.values
            .sortedByDescending { it.time }
            .take(limit)
    }

    override suspend fun submitSurvivalRecord(
        playerId: String,
        playerName: String,
        time: Int
    ) {
        val currentRecord = recordsById[playerId]

        if (currentRecord == null || time > currentRecord.time) {
            val record = LeaderboardRecord(
                id = playerId,
                playerName = playerName,
                time = time
            )

            recordsById[playerId] = record
            lastSubmittedRecord = record
        }
    }
}