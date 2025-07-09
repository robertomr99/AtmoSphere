package com.robertomr99.atmosphere.usecases

import com.robertomr99.atmosphere.domain.ForecastResult
import com.robertomr99.atmosphere.domain.WeatherResult
import com.robertomr99.atmosphere.data.repository.WeatherRepository
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