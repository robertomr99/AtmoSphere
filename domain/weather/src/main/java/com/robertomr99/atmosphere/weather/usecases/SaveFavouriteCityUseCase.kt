package com.robertomr99.atmosphere.weather.usecases

import com.robertomr99.atmosphere.weather.data.WeatherRepository
import com.robertomr99.atmosphere.weather.entities.ForecastResult
import com.robertomr99.atmosphere.weather.entities.WeatherResult
import kotlinx.coroutines.flow.Flow

class SaveFavouriteCityUseCase(
    private val repository: WeatherRepository
){
    suspend operator fun invoke(
        weatherResult: Flow<WeatherResult>,
        forecastResult: Flow<ForecastResult>,
        temperatureUnit: String
    ): Unit =  repository.saveFavouriteCity(weatherResult, forecastResult, temperatureUnit)
}