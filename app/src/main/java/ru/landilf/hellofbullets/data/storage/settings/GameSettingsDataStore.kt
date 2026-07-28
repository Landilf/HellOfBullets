package ru.landilf.hellofbullets.data.storage.settings

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private const val GAME_SETTINGS_DATA_STORE_NAME = "game_settings"

val Context.gameSettingsDataStore by preferencesDataStore(
    name = GAME_SETTINGS_DATA_STORE_NAME
)