package com.robertomr99.atmosphere.domain.weather.usecases

import com.robertomr99.atmosphere.domain.weather.data.WeatherRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class FindFavCityUseCaseTest{

    @Test
    fun `Invoke calls repository`(): Unit = runBlocking{
        val repository = mock<WeatherRepository>()
        val useCase = FindFavCityUseCase(repository)

        useCase("Madrid", "ES")
        verify(repository).findIfCityIsFav("Madrid", "ES")
    }

}