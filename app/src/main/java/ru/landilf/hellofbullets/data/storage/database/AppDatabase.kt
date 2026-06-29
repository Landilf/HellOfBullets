package ru.landilf.hellofbullets.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.landilf.hellofbullets.data.storage.dao.LeaderboardDao
import ru.landilf.hellofbullets.data.storage.dao.PlayerDao
import ru.landilf.hellofbullets.data.storage.entities.leaderboard.LeaderboardRecordEntity
import ru.landilf.hellofbullets.data.storage.entities.player.PlayerProfileEntity

@Database(
    entities = [
        PlayerProfileEntity::class,
        LeaderboardRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun leaderboardDao(): LeaderboardDao
}