package com.robertomr99.atmosphere.data.datasource

import com.robertomr99.atmosphere.domain.ForecastEntity
import com.robertomr99.atmosphere.domain.WeatherEntity
import com.robertomr99.atmosphere.domain.WeatherWithForecasts
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    val weatherList: Flow<List<WeatherEntity>>
    fun findWeatherFavByCity(cityName: String, country: String): Flow<Int?>
    fun getWeatherWithForecastsByCity(cityName: String, country: String): Flow<WeatherWithForecasts?>
    suspend fun saveWeatherWithForecasts(weatherEntity: WeatherEntity, forecastList: List<ForecastEntity>)
    suspend fun saveWeather(weatherEntity: WeatherEntity)
    suspend fun deleteCityByName(cityName: String, country: String)
}


