package com.robertomr99.atmosphere.data.datasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.robertomr99.atmosphere.data.ForecastEntity
import com.robertomr99.atmosphere.data.WeatherEntity
import com.robertomr99.atmosphere.data.datasource.local.WeatherWithForecasts
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWeather(weather: WeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveForecast(forecastList: List<ForecastEntity>)

    @Transaction
    suspend fun saveWeatherWithForecasts(weather: WeatherEntity, forecasts: List<ForecastEntity>) {
        // Primero eliminar forecasts antiguos para esta ciudad
        deleteForecastsByCityId(weather.cityId)
        // Guardar weather
        saveWeather(weather)
        // Guardar forecasts
        if (forecasts.isNotEmpty()) {
            saveForecast(forecasts)
        }
    }

    @Query("DELETE FROM ForecastEntity WHERE cityOwnerId = :cityId")
    suspend fun deleteForecastsByCityId(cityId: String)

    @Transaction
    @Query("DELETE FROM WeatherEntity WHERE name = :cityName AND country = :country")
    suspend fun deleteWeatherByNameAndCountry(cityName: String, country: String)

    @Transaction
    @Query("SELECT * FROM WeatherEntity WHERE name = :cityName AND country = :country LIMIT 1")
    fun getWeatherWithForecastsByCity(cityName: String, country: String): Flow<WeatherWithForecasts?>

    @Query("SELECT COUNT(*) FROM WeatherEntity WHERE name = :cityName AND country = :country LIMIT 1")
    fun findWeatherFavByCity(cityName: String, country: String): Flow<Int?>

    @Query("SELECT * FROM WeatherEntity")
    fun getAll(): Flow<List<WeatherEntity>>

    @Query("SELECT COUNT(*) FROM ForecastEntity WHERE cityOwnerId = :cityId")
    suspend fun getForecastCountByCityId(cityId: String): Int
}