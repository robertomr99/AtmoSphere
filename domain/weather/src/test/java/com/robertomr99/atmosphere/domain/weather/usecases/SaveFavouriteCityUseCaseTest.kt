package com.robertomr99.atmosphere.domain.weather.usecases

import com.robertomr99.atmosphere.domain.weather.data.WeatherRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class SaveFavouriteCityUseCaseTest{

    @Test
    fun `Invoke calls repository`(): Unit = runBlocking{
        val repository = mock<WeatherRepository>()
        val useCase = SaveFavouriteCityUseCase(repository)

        val weatherResultFlow = flowOf(sampleWeatherResult())
        val forecastResultFlow = flowOf(sampleForecastResult())
        val temperatureUnit = "metric"

        useCase(
            weatherResult = weatherResultFlow,
            forecastResult = forecastResultFlow,
            temperatureUnit = temperatureUnit
        )

        verify(repository).saveFavouriteCity(
            weatherResult = weatherResultFlow,
            forecastResult = forecastResultFlow,
            temperatureUnit = temperatureUnit
        )
    }

}