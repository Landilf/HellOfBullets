package ru.landilf.hellofbullets.data.storage.entities.player

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "player_skill_progress",
    primaryKeys = ["playerId", "skillId"],
    foreignKeys = [
        ForeignKey(
            entity = PlayerProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlayerSkillProgressEntity(
    val playerId: Long,
    val skillId: Long,
    val level: Int
)