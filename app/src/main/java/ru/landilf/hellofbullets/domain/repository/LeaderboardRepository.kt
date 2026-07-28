package ru.landilf.hellofbullets.domain.repository

import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord

interface LeaderboardRepository {
    suspend fun getLeaderboard(): List<LeaderboardRecord>
    suspend fun getRecordById(id: String): LeaderboardRecord?
    suspend fun upsertRecord(record: LeaderboardRecord)
    suspend fun replaceLeaderboard(records: List<LeaderboardRecord>)
    suspend fun clearLeaderboard()
}