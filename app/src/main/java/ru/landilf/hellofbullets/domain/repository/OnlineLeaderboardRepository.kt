package ru.landilf.hellofbullets.domain.repository

import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord

interface OnlineLeaderboardRepository {
    suspend fun getOrCreatePlayerId(): String

    suspend fun getTopSurvivalRecords(
        limit: Int
    ): List<LeaderboardRecord>

    suspend fun submitSurvivalRecord(
        playerId: String,
        playerName: String,
        time: Int
    )
}