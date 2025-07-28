package com.robertomr99.atmosphere.domain.weather.data


import com.robertomr99.atmosphere.domain.weather.entities.ForecastEntity
import com.robertomr99.atmosphere.domain.weather.entities.WeatherEntity
import com.robertomr99.atmosphere.domain.weather.entities.WeatherWithForecasts
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    val weatherList: Flow<List<WeatherEntity>>
    fun findWeatherFavByCity(cityName: String, country: String): Flow<Int?>
    fun getWeatherWithForecastsByCity(cityName: String, country: String): Flow<WeatherWithForecasts?>
    suspend fun saveWeatherWithForecasts(weatherEntity: WeatherEntity, forecastList: List<ForecastEntity>)
    suspend fun saveWeather(weatherEntity: WeatherEntity)
    suspend fun deleteCityByName(cityName: String, country: String)
}


