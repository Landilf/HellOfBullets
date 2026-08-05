package ru.landilf.hellofbullets.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.landilf.hellofbullets.data.catalog.equipment.StaticEquipmentDefinitionRepository
import ru.landilf.hellofbullets.data.catalog.equipment.StaticEquipmentQualityDistributionRepository
import ru.landilf.hellofbullets.data.catalog.equipment.StaticEquipmentStatConfigRepository
import ru.landilf.hellofbullets.data.remote.FirestoreOnlineLeaderboardRepository
import ru.landilf.hellofbullets.data.storage.generator.RoomEquipmentItemIdGenerator
import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository
import ru.landilf.hellofbullets.domain.repository.PlayerRepository
import ru.landilf.hellofbullets.data.storage.repository.LeaderboardRepositoryImpl
import ru.landilf.hellofbullets.data.storage.repository.PlayerRepositoryImpl
import ru.landilf.hellofbullets.data.storage.repository.SettingsRepositoryImpl
import ru.landilf.hellofbullets.data.storage.repository.ShopRepositoryImpl
import ru.landilf.hellofbullets.domain.generator.EquipmentItemIdGenerator
import ru.landilf.hellofbullets.domain.repository.EquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.repository.EquipmentQualityDistributionRepository
import ru.landilf.hellofbullets.domain.repository.EquipmentStatConfigRepository
import ru.landilf.hellofbullets.domain.repository.OnlineLeaderboardRepository
import ru.landilf.hellofbullets.domain.repository.SettingsRepository
import ru.landilf.hellofbullets.domain.repository.ShopRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPlayerRepository(
        impl: PlayerRepositoryImpl
    ): PlayerRepository

    @Binds
    abstract fun bindEquipmentDefinitionRepository(
        impl: StaticEquipmentDefinitionRepository
    ): EquipmentDefinitionRepository

    @Binds
    abstract fun bindEquipmentStatConfigRepository(
        impl: StaticEquipmentStatConfigRepository
    ): EquipmentStatConfigRepository

    @Binds
    abstract fun bindEquipmentItemIdGenerator(
        impl: RoomEquipmentItemIdGenerator
    ): EquipmentItemIdGenerator

    @Binds
    abstract fun bindEquipmentQualityDistributionRepository(
        impl: StaticEquipmentQualityDistributionRepository
    ): EquipmentQualityDistributionRepository

    @Binds
    abstract fun bindLeaderRepository(
        impl: LeaderboardRepositoryImpl
    ): LeaderboardRepository

    @Binds
    abstract fun bindOnlineLeaderboardRepository(
        impl: FirestoreOnlineLeaderboardRepository
    ): OnlineLeaderboardRepository

    @Binds
    abstract fun bindShopRepository(
        impl: ShopRepositoryImpl
    ): ShopRepository

    @Binds
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}