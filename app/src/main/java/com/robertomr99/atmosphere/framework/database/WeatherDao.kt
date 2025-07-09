package com.robertomr99.atmosphere.framework.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWeather(weather: DbWeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveForecast(forecastList: List<DbForecastEntity>)

    @Transaction
    suspend fun saveWeatherWithForecasts(weather: DbWeatherEntity, forecasts: List<DbForecastEntity>) {
        // Primero eliminar forecasts antiguos para esta ciudad
        deleteForecastsByCityId(weather.cityId)
        // Guardar weather
        saveWeather(weather)
        // Guardar forecasts
        if (forecasts.isNotEmpty()) {
            saveForecast(forecasts)
        }
    }

    @Query("DELETE FROM DbForecastEntity WHERE cityOwnerId = :cityId")
    suspend fun deleteForecastsByCityId(cityId: String)

    @Transaction
    @Query("DELETE FROM DbWeatherEntity WHERE name = :cityName AND country = :country")
    suspend fun deleteWeatherByNameAndCountry(cityName: String, country: String)

    @Transaction
    @Query("SELECT * FROM DbWeatherEntity WHERE name = :cityName AND country = :country LIMIT 1")
    fun getWeatherWithForecastsByCity(cityName: String, country: String): Flow<DbWeatherWithForecastsEntity>

    @Query("SELECT COUNT(*) FROM DbWeatherEntity WHERE name = :cityName AND country = :country LIMIT 1")
    fun findWeatherFavByCity(cityName: String, country: String): Flow<Int?>

    @Query("SELECT * FROM DbWeatherEntity")
    fun getAll(): Flow<List<DbWeatherEntity>>

    @Query("SELECT COUNT(*) FROM DbForecastEntity WHERE cityOwnerId = :cityId")
    suspend fun getForecastCountByCityId(cityId: String): Int
}