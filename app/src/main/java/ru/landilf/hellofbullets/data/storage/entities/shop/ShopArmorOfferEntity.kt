package ru.landilf.hellofbullets.data.storage.entities.shop

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_armor_offers")
data class ShopArmorOfferEntity(
    @PrimaryKey
    val itemId: Long,
    val position: Int,
    val definitionId: Long,
    val level: Int,
    val qualityName: String,
    val additionalStatTypeName: String,
    val additionalStatValue: Float,
    val hp: Float,
    val defense: Float,
    val specializationCoef: Float,
    val purchasePrice: Int,
    val isSold: Boolean
)
