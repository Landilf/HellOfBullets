package ru.landilf.hellofbullets.data.storage.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.landilf.hellofbullets.data.storage.entities.shop.ShopArmorOfferEntity
import ru.landilf.hellofbullets.data.storage.entities.shop.ShopArtifactOfferEntity
import ru.landilf.hellofbullets.data.storage.entities.shop.ShopStateEntity
import ru.landilf.hellofbullets.data.storage.entities.shop.ShopWeaponOfferEntity

@Dao
interface ShopDao {
    @Query("SELECT * FROM shop_state LIMIT 1")
    suspend fun getShopState(): ShopStateEntity?

    @Query("SELECT * FROM shop_weapon_offers ORDER BY position ASC")
    suspend fun getWeaponOffers(): List<ShopWeaponOfferEntity>

    @Query("SELECT * FROM shop_armor_offers ORDER BY position ASC")
    suspend fun getArmorOffers(): List<ShopArmorOfferEntity>

    @Query("SELECT * FROM shop_artifact_offers ORDER BY position ASC")
    suspend fun getArtifactOffers(): List<ShopArtifactOfferEntity>

    @Query("SELECT * FROM shop_state LIMIT 1")
    fun observeShopState(): Flow<ShopStateEntity?>

    @Query("SELECT * FROM shop_weapon_offers ORDER BY position ASC")
    fun observeWeaponOffers(): Flow<List<ShopWeaponOfferEntity>>

    @Query("SELECT * FROM shop_armor_offers ORDER BY position ASC")
    fun observeArmorOffers(): Flow<List<ShopArmorOfferEntity>>

    @Query("SELECT * FROM shop_artifact_offers ORDER BY position ASC")
    fun observeArtifactOffers(): Flow<List<ShopArtifactOfferEntity>>

    @Upsert
    suspend fun upsertShopState(shopState: ShopStateEntity)

    @Upsert
    suspend fun upsertWeaponOffers(offers: List<ShopWeaponOfferEntity>)

    @Upsert
    suspend fun upsertArmorOffers(offers: List<ShopArmorOfferEntity>)

    @Upsert
    suspend fun upsertArtifactOffers(offers: List<ShopArtifactOfferEntity>)

    @Transaction
    suspend fun replaceShop(
        shopState: ShopStateEntity,
        weaponOffers: List<ShopWeaponOfferEntity>,
        armorOffers: List<ShopArmorOfferEntity>,
        artifactOffers: List<ShopArtifactOfferEntity>
    ) {
        clearWeaponOffers()
        clearArmorOffers()
        clearArtifactOffers()

        upsertShopState(shopState)
        upsertWeaponOffers(weaponOffers)
        upsertArmorOffers(armorOffers)
        upsertArtifactOffers(artifactOffers)
    }

    @Query("DELETE FROM shop_weapon_offers")
    suspend fun clearWeaponOffers()

    @Query("DELETE FROM shop_armor_offers")
    suspend fun clearArmorOffers()

    @Query("DELETE FROM shop_artifact_offers")
    suspend fun clearArtifactOffers()
}