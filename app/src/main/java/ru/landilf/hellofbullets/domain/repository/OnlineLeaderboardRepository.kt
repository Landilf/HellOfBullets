package ru.landilf.hellofbullets.domain.repository

import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord

interface OnlineLeaderboardRepository {
    suspend fun getTopSurvivalRecords(
        limit: Int
    ): List<LeaderboardRecord>

    suspend fun submitSurvivalRecord(
        playerName: String,
        time: Int
    )
}