package ru.landilf.hellofbullets.data.storage.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ru.landilf.hellofbullets.data.storage.entities.leaderboard.LeaderboardRecordEntity

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard ORDER BY time DESC LIMIT 20")
    suspend fun getLeaderboard(): List<LeaderboardRecordEntity>

    @Query("SELECT * FROM leaderboard WHERE id = :id LIMIT 1")
    suspend fun getRecordById(id: String): LeaderboardRecordEntity?

    @Query("SELECT id FROM leaderboard WHERE id IN (:ids)")
    suspend fun getExistingRecordIds(
        ids: List<String>
    ): List<String>

    @Upsert
    suspend fun upsert(record: LeaderboardRecordEntity)

    @Query("DELETE FROM leaderboard")
    suspend fun clearLeaderboard()
}