package ru.landilf.hellofbullets.data.storage.entities.equipment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.landilf.hellofbullets.data.storage.entities.player.PlayerProfileEntity

@Entity(
    tableName = "artifact_items",
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
data class ArtifactItemEntity(
    @PrimaryKey
    val id: Long,
    val ownerId: Long,
    val definitionId: Long,
    val level: Int,
    val qualityName: String,
    val additionalStatTypeName: String,
    val additionalStatValue: Float,
    val cooldownReductionPercent: Float,
    val durationBonusPercent: Float,
    @ColumnInfo(defaultValue = "0.0")
    val specializationCoef: Float
)
