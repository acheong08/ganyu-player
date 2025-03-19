package dev.duti.ganyu.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object PreferencesKeys {
    val INVIDIOUS_COOKIE = stringPreferencesKey("invidious_cookie")
}

class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    suspend fun saveIvCookie(cookie: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.INVIDIOUS_COOKIE] = cookie
        }
    }

    // Get the cookie
    suspend fun getCookie(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.INVIDIOUS_COOKIE]
        }.first()
    }
}