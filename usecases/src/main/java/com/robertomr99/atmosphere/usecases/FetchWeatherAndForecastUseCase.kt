package com.robertomr99.atmosphere.usecases

import com.robertomr99.atmosphere.domain.ForecastResult
import com.robertomr99.atmosphere.domain.WeatherResult
import com.robertomr99.atmosphere.data.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow

class FetchWeatherAndForecastUseCase(
    private val repository: WeatherRepository
){
    operator fun invoke(
        cityName: String,
        country: String,
        temperatureUnit: String,
        isFavCity: Boolean
    ): Flow<Pair<WeatherResult?, ForecastResult?>> = repository.getWeatherAndForecastForCity(cityName, country, temperatureUnit, isFavCity)
}