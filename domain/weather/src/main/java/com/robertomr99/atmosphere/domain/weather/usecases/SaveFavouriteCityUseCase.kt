package com.robertomr99.atmosphere.domain.weather.usecases

import com.robertomr99.atmosphere.domain.weather.data.WeatherRepository
import com.robertomr99.atmosphere.domain.weather.entities.ForecastResult
import com.robertomr99.atmosphere.domain.weather.entities.WeatherResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveFavouriteCityUseCase @Inject constructor(
    private val repository: WeatherRepository
){
    suspend operator fun invoke(
        weatherResult: Flow<WeatherResult>,
        forecastResult: Flow<ForecastResult>,
        temperatureUnit: String
    ): Unit =  repository.saveFavouriteCity(weatherResult, forecastResult, temperatureUnit)
}