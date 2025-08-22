package com.robertomr99.atmosphere.feature.home

import app.cash.turbine.test
import com.robertomr99.atmosphere.data.FakeRegionDataSource
import com.robertomr99.atmosphere.data.buildWeatherRepositoryWith
import com.robertomr99.atmosphere.domain.region.usecases.GetCurrentRegionUseCase
import com.robertomr99.atmosphere.domain.weather.entities.WeatherEntity
import com.robertomr99.atmosphere.domain.weather.entities.WeatherResult
import com.robertomr99.atmosphere.domain.weather.usecases.DeleteFavouriteCityUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FetchFavouritesCitiesUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FetchSuggestionsForCityUseCase
import com.robertomr99.atmosphere.testrule.CoroutineTestRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import com.robertomr99.atmosphere.feature.common.Result
import com.robertomr99.atmosphere.sampleWeatherEntity
import com.robertomr99.atmosphere.sampleWeatherResult
import junit.framework.TestCase.assertTrue

@RunWith(MockitoJUnitRunner::class)
class HomeIntegrationTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Test
    fun `data is loaded from server when local data source is empty`() = runTest {
        val remoteData = sampleWeatherResult("Madrid")
        val vm = buildViewModelWith(
            localData = emptyList(),
            remoteData = remoteData
        )

        vm.state.test {
            assertEquals(Result.Loading, awaitItem())
            assertEquals(Result.Success(emptyList<WeatherEntity>()), awaitItem())
        }
    }

    @Test
    fun `data is loaded from local when local data exists`() = runTest {
        val localData = listOf(
            sampleWeatherEntity("Madrid", "ES"),
            sampleWeatherEntity("Barcelona", "ES")
        )
        val vm = buildViewModelWith(localData = localData)

        vm.state.test {
            assertEquals(Result.Loading, awaitItem())

            val result = awaitItem()
            assertTrue("Expected Result.Success", result is Result.Success)

            val cities = (result as Result.Success).data
            assertEquals("Should load 2 cities from local data", 2, cities.size)
        }
    }
}

private fun buildViewModelWith(
    localData: List<WeatherEntity> = emptyList(),
    remoteData: WeatherResult? = null
): HomeViewModel {
    val repository = buildWeatherRepositoryWith(
        localWeatherData = localData,
        remoteWeatherResult = remoteData
    )

    val fetchFavouritesCitiesUseCase = FetchFavouritesCitiesUseCase(repository)
    val fetchSuggestionsForCityUseCase = FetchSuggestionsForCityUseCase(repository)
    val deleteFavouriteCityUseCase = DeleteFavouriteCityUseCase(repository)
    val getCurrentRegionUseCase = GetCurrentRegionUseCase(FakeRegionDataSource())

    return HomeViewModel(
        fetchFavouritesCitiesUseCase = fetchFavouritesCitiesUseCase,
        fetchSuggestionsForCityUseCase = fetchSuggestionsForCityUseCase,
        deleteFavouriteCityUseCase = deleteFavouriteCityUseCase,
        getCurrentRegionUseCase = getCurrentRegionUseCase
    )
}
