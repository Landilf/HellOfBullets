package ru.landilf.hellofbullets.data.storage.entities.shop

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_state")
data class ShopStateEntity(
    @PrimaryKey
    val id: Int = 0,
    val lastAutomaticRefreshEpochDay: Long,
    val manualRefreshCount: Int
)