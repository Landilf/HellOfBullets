package ru.landilf.hellofbullets.data.storage.entities.equipment

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.landilf.hellofbullets.data.storage.entities.player.PlayerProfileEntity

@Entity(
    tableName = "armor_items",
    foreignKeys = [
        ForeignKey(
            entity = PlayerProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ownerId")]
)
data class ArmorItemEntity(
    @PrimaryKey
    val id: Long,
    val ownerId: Long,
    val definitionId: Long,
    val level: Int,
    val qualityName: String,
    val additionalStatTypeName: String,
    val additionalStatValue: Float,
    val hp: Float,
    val defense: Float
)
