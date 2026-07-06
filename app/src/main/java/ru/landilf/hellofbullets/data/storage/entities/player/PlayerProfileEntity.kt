package ru.landilf.hellofbullets.data.storage.entities.player

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val level: Int,
    val expAmount: Int,
    val silverAmount: Int,
)
