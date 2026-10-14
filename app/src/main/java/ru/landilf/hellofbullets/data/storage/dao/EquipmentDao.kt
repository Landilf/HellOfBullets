package ru.landilf.hellofbullets.data.storage.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.landilf.hellofbullets.data.storage.entities.equipment.ArmorItemEntity
import ru.landilf.hellofbullets.data.storage.entities.equipment.ArtifactItemEntity
import ru.landilf.hellofbullets.data.storage.entities.equipment.WeaponItemEntity

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM weapon_items WHERE ownerId = :ownerId ORDER BY id ASC")
    suspend fun getWeaponItems(ownerId: Long): List<WeaponItemEntity>

    @Query("SELECT * FROM armor_items WHERE ownerId = :ownerId ORDER BY id ASC")
    suspend fun getArmorItems(ownerId: Long): List<ArmorItemEntity>

    @Query("SELECT * FROM artifact_items WHERE ownerId = :ownerId ORDER BY id ASC")
    suspend fun getArtifactItems(ownerId: Long): List<ArtifactItemEntity>

    @Query("SELECT * FROM weapon_items WHERE ownerId = :ownerId ORDER BY id ASC")
    fun observeWeaponItems(ownerId: Long): Flow<List<WeaponItemEntity>>

    @Query("SELECT * FROM armor_items WHERE ownerId = :ownerId ORDER BY id ASC")
    fun observeArmorItems(ownerId: Long): Flow<List<ArmorItemEntity>>

    @Query("SELECT * FROM artifact_items WHERE ownerId = :ownerId ORDER BY id ASC")
    fun observeArtifactItems(ownerId: Long): Flow<List<ArtifactItemEntity>>

    @Upsert
    suspend fun upsertWeaponItems(items: List<WeaponItemEntity>)

    @Upsert
    suspend fun upsertArmorItems(items: List<ArmorItemEntity>)

    @Upsert
    suspend fun upsertArtifactItems(items: List<ArtifactItemEntity>)

    @Transaction
    suspend fun replaceEquipment(
        ownerId: Long,
        weaponItems: List<WeaponItemEntity>,
        armorItems: List<ArmorItemEntity>,
        artifactItems: List<ArtifactItemEntity>,
    ) {
        clearWeaponItems(ownerId)
        clearArmorItems(ownerId)
        clearArtifactItems(ownerId)

        upsertWeaponItems(weaponItems)
        upsertArmorItems(armorItems)
        upsertArtifactItems(artifactItems)
    }

    @Query("DELETE FROM weapon_items WHERE ownerId = :ownerId")
    suspend fun clearWeaponItems(ownerId: Long)

    @Query("DELETE FROM armor_items WHERE ownerId = :ownerId")
    suspend fun clearArmorItems(ownerId: Long)

    @Query("DELETE FROM artifact_items WHERE ownerId = :ownerId")
    suspend fun clearArtifactItems(ownerId: Long)
}
