package ru.landilf.hellofbullets.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.landilf.hellofbullets.data.remote.FirestoreOnlineLeaderboardRepository
import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository
import ru.landilf.hellofbullets.domain.repository.PlayerRepository
import ru.landilf.hellofbullets.data.storage.repository.LeaderboardRepositoryImpl
import ru.landilf.hellofbullets.data.storage.repository.PlayerRepositoryImpl
import ru.landilf.hellofbullets.domain.repository.OnlineLeaderboardRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPlayerRepository(
        impl: PlayerRepositoryImpl
    ): PlayerRepository

    @Binds
    abstract fun bindLeaderRepository(
        impl: LeaderboardRepositoryImpl
    ): LeaderboardRepository

    @Binds
    abstract fun bindOnlineLeaderboardRepository(
        impl: FirestoreOnlineLeaderboardRepository
    ): OnlineLeaderboardRepository
}