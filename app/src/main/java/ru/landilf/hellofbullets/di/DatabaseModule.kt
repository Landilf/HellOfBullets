package ru.landilf.hellofbullets.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.landilf.hellofbullets.data.storage.dao.EquipmentDao
import ru.landilf.hellofbullets.data.storage.dao.EquipmentItemIdDao
import ru.landilf.hellofbullets.data.storage.dao.LeaderboardDao
import ru.landilf.hellofbullets.data.storage.dao.PlayerDao
import ru.landilf.hellofbullets.data.storage.dao.ShopDao
import ru.landilf.hellofbullets.data.storage.database.AppDatabase
import ru.landilf.hellofbullets.data.storage.database.DatabaseMigrations
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "hell_of_bullets.db"
        )
            .addMigrations(
                DatabaseMigrations.MIGRATION_2_3,
                DatabaseMigrations.MIGRATION_3_4,
                DatabaseMigrations.MIGRATION_4_5,
                DatabaseMigrations.MIGRATION_5_6,
                DatabaseMigrations.MIGRATION_6_7,
                DatabaseMigrations.MIGRATION_7_8
            )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun providePlayerDao(database: AppDatabase): PlayerDao {
        return database.playerDao()
    }

    @Provides
    fun provideEquipmentDao(database: AppDatabase): EquipmentDao {
        return database.equipmentDao()
    }

    @Provides
    fun provideEquipmentItemIdDao(database: AppDatabase): EquipmentItemIdDao {
        return database.equipmentItemIdDao()
    }

    @Provides
    fun provideLeaderboardDao(database: AppDatabase): LeaderboardDao {
        return database.leaderboardDao()
    }

    @Provides
    fun provideShopDao(database: AppDatabase): ShopDao {
        return database.shopDao()
    }
}