package com.robertomr99.atmosphere.domain.weather.usecases

import com.robertomr99.atmosphere.sampleForecastResult
import com.robertomr99.atmosphere.sampleWeatherResult
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class FetchWeatherAndForecastUseCaseTest {

    @Test
    fun `Invoke calls repository`() {

        val pairWeatherForecast = flowOf(Pair(sampleWeatherResult(), sampleForecastResult()))

        val useCase = FetchWeatherAndForecastUseCase(mock {
            on { getWeatherAndForecastForCity("Madrid", "ES", "metric", false ) } doReturn pairWeatherForecast
        })

        val result = useCase("Madrid", "ES", "metric", false)

        assertEquals(pairWeatherForecast, result)
    }
}

