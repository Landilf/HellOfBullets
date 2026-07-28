package ru.landilf.hellofbullets.data.storage.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ru.landilf.hellofbullets.domain.model.settings.GameSettings
import ru.landilf.hellofbullets.domain.repository.SettingsRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    override fun observeSettings(): Flow<GameSettings> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val inputSensitivity = preferences[INPUT_SENSITIVITY_KEY]
                    ?.coerceIn(
                        GameSettings.MIN_INPUT_SENSITIVITY,
                        GameSettings.MAX_INPUT_SENSITIVITY
                    )
                    ?: GameSettings.DEFAULT_INPUT_SENSITIVITY

                GameSettings(
                    inputSensitivity = inputSensitivity
                )
            }
    }

    override suspend fun updateInputSensitivity(
        inputSensitivity: Float
    ) {
        require(
            inputSensitivity in
                    GameSettings.MIN_INPUT_SENSITIVITY..GameSettings.MAX_INPUT_SENSITIVITY
        )

        dataStore.edit { preferences ->
            preferences[INPUT_SENSITIVITY_KEY] = inputSensitivity
        }
    }

    private companion object {
        val INPUT_SENSITIVITY_KEY = floatPreferencesKey("input_sensitivity")
    }
}