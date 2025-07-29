package com.robertomr99.atmosphere.domain.weather.usecases


import com.robertomr99.atmosphere.domain.weather.data.WeatherRepository
import com.robertomr99.atmosphere.domain.weather.entities.ForecastResult
import com.robertomr99.atmosphere.domain.weather.entities.WeatherResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchWeatherAndForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
){
    operator fun invoke(
        cityName: String,
        country: String,
        temperatureUnit: String,
        isFavCity: Boolean
    ): Flow<Pair<WeatherResult?, ForecastResult?>> = repository.getWeatherAndForecastForCity(cityName, country, temperatureUnit, isFavCity)
}