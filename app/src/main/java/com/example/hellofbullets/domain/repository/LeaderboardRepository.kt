package com.example.hellofbullets.domain.repository

import com.example.hellofbullets.domain.model.leaderboard.LeaderboardRecord

interface LeaderboardRepository {
    suspend fun getLeaderboard(): List<LeaderboardRecord>
    suspend fun getRecordByPlayerName(playerName: String): LeaderboardRecord?
    suspend fun upsertRecord(record: LeaderboardRecord)
    suspend fun clearLeaderboard()
}