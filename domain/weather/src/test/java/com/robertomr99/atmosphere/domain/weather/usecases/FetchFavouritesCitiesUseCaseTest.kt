package com.robertomr99.atmosphere.domain.weather.usecases

import com.robertomr99.atmosphere.sampleWeatherResultList
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class FetchFavouritesCitiesUseCaseTest{

    @Test
    fun `Invoke calls repository`() {

        val listOfWeatherResult = flowOf(sampleWeatherResultList())

        val useCase = FetchFavouritesCitiesUseCase(mock {
            on { getWeatherForFavouritesCities("metric") } doReturn listOfWeatherResult
        })

        val result = useCase("metric")

        assertEquals(listOfWeatherResult, result)
    }
}