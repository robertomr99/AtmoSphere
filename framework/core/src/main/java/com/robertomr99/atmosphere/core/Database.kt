package com.robertomr99.atmosphere.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.robertomr99.atmosphere.weather.database.DbForecastEntity
import com.robertomr99.atmosphere.weather.database.DbWeatherEntity
import com.robertomr99.atmosphere.weather.database.WeatherDao

@Database(
    entities = [DbWeatherEntity::class, DbForecastEntity::class],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}