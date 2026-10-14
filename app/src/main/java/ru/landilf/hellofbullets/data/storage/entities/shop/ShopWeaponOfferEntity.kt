package ru.landilf.hellofbullets.data.storage.entities.shop

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_weapon_offers")
data class ShopWeaponOfferEntity(
    @PrimaryKey
    val itemId: Long,
    val position: Int,
    val definitionId: Long,
    val level: Int,
    val qualityName: String,
    val additionalStatTypeName: String,
    val additionalStatValue: Float,
    val damage: Float,
    val attackSpeed: Float,
    val specializationCoef: Float,
    val purchasePrice: Int,
    val isSold: Boolean
)