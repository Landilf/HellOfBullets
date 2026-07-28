package ru.landilf.hellofbullets.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.landilf.hellofbullets.data.storage.settings.gameSettingsDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsDataStoreModule {

    @Provides
    @Singleton
    fun provideGameSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.gameSettingsDataStore
    }
}