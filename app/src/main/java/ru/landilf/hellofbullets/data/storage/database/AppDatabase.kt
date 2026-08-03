package ru.landilf.hellofbullets.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.landilf.hellofbullets.data.storage.dao.EquipmentDao
import ru.landilf.hellofbullets.data.storage.dao.EquipmentItemIdDao
import ru.landilf.hellofbullets.data.storage.dao.LeaderboardDao
import ru.landilf.hellofbullets.data.storage.dao.PlayerDao
import ru.landilf.hellofbullets.data.storage.entities.equipment.ArmorItemEntity
import ru.landilf.hellofbullets.data.storage.entities.equipment.ArtifactItemEntity
import ru.landilf.hellofbullets.data.storage.entities.equipment.EquipmentItemIdCounterEntity
import ru.landilf.hellofbullets.data.storage.entities.equipment.WeaponItemEntity
import ru.landilf.hellofbullets.data.storage.entities.leaderboard.LeaderboardRecordEntity
import ru.landilf.hellofbullets.data.storage.entities.player.PlayerBuildEntity
import ru.landilf.hellofbullets.data.storage.entities.player.PlayerProfileEntity

@Database(
    entities = [
        PlayerProfileEntity::class,
        PlayerBuildEntity::class,
        WeaponItemEntity::class,
        ArmorItemEntity::class,
        ArtifactItemEntity::class,
        EquipmentItemIdCounterEntity::class,
        LeaderboardRecordEntity::class
    ],
    version = 5,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun equipmentDao(): EquipmentDao
    abstract fun equipmentItemIdDao(): EquipmentItemIdDao
    abstract fun leaderboardDao(): LeaderboardDao
}