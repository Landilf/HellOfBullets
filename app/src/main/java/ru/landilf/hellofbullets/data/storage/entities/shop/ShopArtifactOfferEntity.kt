package ru.landilf.hellofbullets.data.storage.entities.shop

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_artifact_offers")
data class ShopArtifactOfferEntity(
    @PrimaryKey
    val itemId: Long,
    val position: Int,
    val definitionId: Long,
    val level: Int,
    val qualityName: String,
    val additionalStatTypeName: String,
    val additionalStatValue: Float,
    val cooldownReductionPercent: Float,
    val durationBonusPercent: Float,
    val specializationCoef: Float,
    val purchasePrice: Int,
    val isSold: Boolean
)