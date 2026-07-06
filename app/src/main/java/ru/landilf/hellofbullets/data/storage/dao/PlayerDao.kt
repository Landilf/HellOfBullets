package ru.landilf.hellofbullets.data.storage.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ru.landilf.hellofbullets.data.storage.entities.player.PlayerProfileEntity

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player_profile LIMIT 1")
    suspend fun getPlayerProfile(): PlayerProfileEntity?

    @Upsert
    suspend fun upsertPlayerProfile(profile: PlayerProfileEntity)

    @Query("DELETE FROM player_profile")
    suspend fun clearPlayerProfile()
}