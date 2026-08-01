package ru.landilf.hellofbullets.data.storage.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.landilf.hellofbullets.data.storage.entities.player.PlayerBuildEntity
import ru.landilf.hellofbullets.data.storage.entities.player.PlayerProfileEntity

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player_profile LIMIT 1")
    suspend fun getPlayerProfile(): PlayerProfileEntity?

    @Query("SELECT * FROM player_build WHERE playerId = :playerId LIMIT 1")
    suspend fun getPlayerBuild(playerId: Long): PlayerBuildEntity?

    @Upsert
    suspend fun upsertPlayerProfile(profile: PlayerProfileEntity)

    @Upsert
    suspend fun upsertPlayerBuild(build: PlayerBuildEntity)

    @Query("DELETE FROM player_profile")
    suspend fun clearPlayerProfile()

    @Query("SELECT * FROM player_profile LIMIT 1")
    fun observePlayerProfile(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_build WHERE playerId = :playerId LIMIT 1")
    fun observePlayerBuild(playerId: Long): Flow<PlayerBuildEntity?>

}