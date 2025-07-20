package com.robertomr99.atmosphere.weather.usecases


import com.robertomr99.atmosphere.weather.data.WeatherRepository
import com.robertomr99.atmosphere.weather.entities.ForecastResult
import com.robertomr99.atmosphere.weather.entities.WeatherResult
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