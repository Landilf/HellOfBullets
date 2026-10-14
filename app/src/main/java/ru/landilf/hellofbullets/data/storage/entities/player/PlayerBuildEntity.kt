package ru.landilf.hellofbullets.data.storage.entities.player

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "player_build",
    foreignKeys = [
        ForeignKey(
            entity = PlayerProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlayerBuildEntity(
    @PrimaryKey
    val playerId: Long,
    val equippedWeaponItemId: Long?,
    val equippedArmorItemId: Long?,
    val equippedArtifactItemId: Long?
)
