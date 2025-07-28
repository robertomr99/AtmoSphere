package com.robertomr99.atmosphere.framework.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.robertomr99.atmosphere.framework.weather.database.DbForecastEntity
import com.robertomr99.atmosphere.framework.weather.database.DbWeatherEntity
import com.robertomr99.atmosphere.framework.weather.database.WeatherDao

@Database(
    entities = [DbWeatherEntity::class, DbForecastEntity::class],
    version = 1,
    exportSchema = false
)
internal abstract class Database : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}