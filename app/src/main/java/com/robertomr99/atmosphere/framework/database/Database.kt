package com.robertomr99.atmosphere.framework.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DbWeatherEntity::class, DbForecastEntity::class],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun WeatherDao(): WeatherDao
}