package com.robertomr99.atmosphere.framework.core

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.preferencesDataStore
import com.robertomr99.atmosphere.domain.weather.IDataStoreManager
import org.koin.core.annotation.Factory

private val Context.dataStore by preferencesDataStore("city_fetch_timestamps")

@Factory
class DataStoreManager(private val context: Context) : IDataStoreManager {

    override fun keyForCity(city: String, country: String, suffix: String): Preferences.Key<Long> {
        val normalizedCity = city.trim().lowercase()
        val normalizedCountry = country.trim().uppercase()
        return longPreferencesKey("timestamp_${normalizedCity}_${normalizedCountry}$suffix")
    }

    override suspend fun saveTimestamp(city: String, country: String, timestamp: Long, suffix: String) {
        context.dataStore.edit { preferences ->
            preferences[keyForCity(city, country, suffix)] = timestamp
        }
    }

    override suspend fun getTimestamp(city: String, country: String, suffix: String): Long? {
        val key = keyForCity(city, country, suffix)
        return context.dataStore.data.map { it[key] }.first()
    }

    override suspend fun deleteTimestamp(city: String, country: String, suffix: String) {
        val key = keyForCity(city, country, suffix)
        context.dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }
}