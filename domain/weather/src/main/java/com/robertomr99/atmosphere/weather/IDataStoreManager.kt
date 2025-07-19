package com.robertomr99.atmosphere.weather

import androidx.datastore.preferences.core.Preferences

interface IDataStoreManager {
    fun keyForCity(city: String, country: String, suffix: String = ""): Preferences.Key<Long>

    suspend fun saveTimestamp(city: String, country: String, timestamp: Long, suffix: String = "")

    suspend fun getTimestamp(city: String, country: String, suffix: String = ""): Long?

    suspend fun deleteTimestamp(city: String, country: String, suffix: String = "")
}