package ru.landilf.hellofbullets.data.storage.entities.leaderboard

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard")
data class LeaderboardRecordEntity(
    @PrimaryKey
    val playerName: String,
    val time: Int
)
