package com.robertomr99.atmosphere.weather.data

import com.robertomr99.atmosphere.weather.entities.CityCoordinatesResponse
import com.robertomr99.atmosphere.weather.entities.ForecastResult
import com.robertomr99.atmosphere.weather.entities.WeatherResult
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {
    fun getWeatherForCity(cityName: String, units: String, region: String): Flow<WeatherResult?>
    fun getForecastForCity(cityName: String, units: String, region: String): Flow<ForecastResult?>
    fun getSuggestionsForCity(cityName: String, region: String): Flow<List<CityCoordinatesResponse>>
}

