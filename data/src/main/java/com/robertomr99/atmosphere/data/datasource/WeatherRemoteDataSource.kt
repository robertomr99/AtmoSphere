package com.robertomr99.atmosphere.data.datasource

import com.robertomr99.atmosphere.domain.CityCoordinatesResponse
import com.robertomr99.atmosphere.domain.ForecastResult
import com.robertomr99.atmosphere.domain.WeatherResult
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {
    fun getWeatherForCity(cityName: String, units: String, region: String): Flow<WeatherResult?>
    fun getForecastForCity(cityName: String, units: String, region: String): Flow<ForecastResult?>
    fun getSuggestionsForCity(cityName: String, region: String): Flow<List<CityCoordinatesResponse>>
}

