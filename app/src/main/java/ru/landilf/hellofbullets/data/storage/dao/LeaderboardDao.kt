package ru.landilf.hellofbullets.data.storage.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import ru.landilf.hellofbullets.data.storage.entities.leaderboard.LeaderboardRecordEntity

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard ORDER BY time DESC LIMIT 20")
    suspend fun getLeaderboard(): List<LeaderboardRecordEntity>

    @Query("SELECT * FROM leaderboard WHERE id = :id LIMIT 1")
    suspend fun getRecordById(id: String): LeaderboardRecordEntity?

    @Upsert
    suspend fun upsert(record: LeaderboardRecordEntity)

    @Upsert
    suspend fun upsertAll(
        records: List<LeaderboardRecordEntity>
    )

    @Transaction
    suspend fun replaceLeaderboard(
        records: List<LeaderboardRecordEntity>
    ) {
        clearLeaderboard()
        upsertAll(records)
    }

    @Query("DELETE FROM leaderboard")
    suspend fun clearLeaderboard()
}