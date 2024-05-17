package com.example.storyapp.data.database

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ganti nama kelas ini nanti, jika membingungkan
class SsPreferences private constructor(private val dataStore: DataStore<Preferences>){

    private val loginKey = booleanPreferencesKey("login_session")
    private val nameKey = stringPreferencesKey("login_name")
    private val tokenKey = stringPreferencesKey("token_session")

    fun getLoginSession(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[loginKey] ?: false
        }
    }

    suspend fun saveLoginSession(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[loginKey] = isDarkModeActive
        }
    }

    suspend fun saveName(string: String){
        dataStore.edit { pref ->
            pref[nameKey] = string
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[tokenKey] ?: ""
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    suspend fun clearLogin() {
        dataStore.edit { preferences ->
            preferences.remove(loginKey)
            preferences.remove(nameKey)
            preferences.remove(tokenKey)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SsPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): SsPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SsPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}