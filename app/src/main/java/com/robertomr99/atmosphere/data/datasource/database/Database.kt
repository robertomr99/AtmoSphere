package com.robertomr99.atmosphere.data.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.robertomr99.atmosphere.data.ForecastEntity
import com.robertomr99.atmosphere.data.WeatherEntity

@Database(
    entities = [WeatherEntity::class, ForecastEntity::class],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun WeatherDao(): WeatherDao
}