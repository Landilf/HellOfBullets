package ru.landilf.hellofbullets.data.storage.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.landilf.hellofbullets.data.storage.entities.player.PlayerSkillProgressEntity

@Dao
interface PlayerSkillDao {
    @Query(
        """
            SELECT * FROM player_skill_progress
            WHERE playerId = :playerId
            ORDER BY skillId ASC
        """
    )
    suspend fun getSkillProgresses(
        playerId: Long
    ): List<PlayerSkillProgressEntity>

    @Upsert
    suspend fun upsertSkillProgresses(
        progressEntities: List<PlayerSkillProgressEntity>
    )

    @Query(
        """
            DELETE FROM player_skill_progress
            WHERE playerId = :playerId
        """
    )
    suspend fun deleteSkillProgresses(
        playerId: Long
    )

    @Query(
        """
            SELECT * FROM player_skill_progress
            WHERE playerId = :playerId
            ORDER BY skillId ASC
        """
    )
    fun observeSkillProgresses(
        playerId: Long
    ): Flow<List<PlayerSkillProgressEntity>>
}