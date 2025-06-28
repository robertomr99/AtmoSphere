package com.robertomr99.atmosphere.data.datasource.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("city_fetch_timestamps")

class DataStoreManager(private val context: Context) {

    private fun keyForCity(city: String, country: String, suffix: String = ""): Preferences.Key<Long> {
        val normalizedCity = city.trim().lowercase()
        val normalizedCountry = country.trim().uppercase()
        return longPreferencesKey("timestamp_${normalizedCity}_${normalizedCountry}$suffix")
    }

    suspend fun saveTimestamp(city: String, country: String, timestamp: Long, suffix: String = "") {
        context.dataStore.edit { preferences ->
            preferences[keyForCity(city, country, suffix)] = timestamp
        }
        logAllPreferences(context)
    }

    suspend fun getTimestamp(city: String, country: String, suffix: String = ""): Long? {
        val key = keyForCity(city, country, suffix)
        return context.dataStore.data.map { it[key] }.first()
    }

    suspend fun deleteTimestamp(city: String, country: String, suffix: String = "") {
        val key = keyForCity(city, country, suffix)
        context.dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    private suspend fun logAllPreferences(context: Context) {
        context.dataStore.data.first().asMap().forEach { (key, value) ->
            Log.i("DataStore", "Key: ${key.name}, Value: $value")
        }
    }
}