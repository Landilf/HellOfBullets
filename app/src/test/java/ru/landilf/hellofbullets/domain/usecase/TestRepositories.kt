package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord
import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository
import ru.landilf.hellofbullets.domain.repository.PlayerRepository

class FakeLeaderboardRepository(
    initialRecords: List<LeaderboardRecord> = emptyList()
) : LeaderboardRepository {
    private val recordsByPlayerName = initialRecords
        .associateBy { it.playerName }
        .toMutableMap()

    var upsertCallCount = 0
        private set

    override suspend fun getLeaderboard(): List<LeaderboardRecord> {
        return recordsByPlayerName.values
            .sortedByDescending { it.time }
            .take(MAX_LEADERBOARD_SIZE)
    }

    override suspend fun getRecordByPlayerName(
        playerName: String
    ): LeaderboardRecord? {
        return recordsByPlayerName[playerName]
    }

    override suspend fun getExistingPlayerNames(
        playerNames: List<String>
    ): Set<String> {
        return recordsByPlayerName.keys.intersect(playerNames.toSet())
    }

    override suspend fun upsertRecord(
        record: LeaderboardRecord
    ) {
        recordsByPlayerName[record.playerName] = record
        upsertCallCount++
    }

    override suspend fun clearLeaderboard() {
        recordsByPlayerName.clear()
    }

    fun getAllRecords(): List<LeaderboardRecord> {
        return recordsByPlayerName.values.toList()
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