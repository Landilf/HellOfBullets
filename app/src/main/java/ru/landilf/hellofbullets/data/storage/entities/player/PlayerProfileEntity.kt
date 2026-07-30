package ru.landilf.hellofbullets.data.storage.entities.player

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val level: Int,
    @field:ColumnInfo(name = "expAmount")
    val totalExperience: Int,
    val silverAmount: Int,
    val skillPointAmount: Int
)
