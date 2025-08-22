package com.robertomr99.atmosphere.feature.home

import app.cash.turbine.test
import com.robertomr99.atmosphere.domain.region.usecases.GetCurrentRegionUseCase
import com.robertomr99.atmosphere.domain.weather.TemperatureUnit
import com.robertomr99.atmosphere.domain.weather.entities.CityCoordinatesResponse
import com.robertomr99.atmosphere.domain.weather.entities.WeatherResult
import com.robertomr99.atmosphere.domain.weather.usecases.DeleteFavouriteCityUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FetchFavouritesCitiesUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FetchSuggestionsForCityUseCase
import com.robertomr99.atmosphere.sampleCityCoordinates
import com.robertomr99.atmosphere.testrule.CoroutineTestRule
import com.robertomr99.atmosphere.feature.common.Result
import com.robertomr99.atmosphere.sampleWeatherResult
import com.robertomr99.atmosphere.sampleWeatherResultList
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest{

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    lateinit var fetchFavouritesCitiesUseCase: FetchFavouritesCitiesUseCase

    @Mock
    lateinit var fetchSuggestionsForCityUseCase: FetchSuggestionsForCityUseCase

    @Mock
    lateinit var deleteFavouriteCityUseCase: DeleteFavouriteCityUseCase

    @Mock
    lateinit var getCurrentRegionUseCase: GetCurrentRegionUseCase

    private lateinit var vm: HomeViewModel

    @Before
    fun setUp() {
        whenever(fetchFavouritesCitiesUseCase("metric")).thenReturn(flowOf(emptyList()))

        vm = HomeViewModel(
            fetchFavouritesCitiesUseCase = fetchFavouritesCitiesUseCase,
            fetchSuggestionsForCityUseCase = fetchSuggestionsForCityUseCase,
            deleteFavouriteCityUseCase = deleteFavouriteCityUseCase,
            getCurrentRegionUseCase = getCurrentRegionUseCase
        )
    }

    @Test
    fun `setTemperatureUnit updates temperature unit`() = runTest {
        vm.setTemperatureUnit(TemperatureUnit.FAHRENHEIT)
        assertEquals(TemperatureUnit.FAHRENHEIT, vm.temperatureUnit.value)
    }

    @Test
    fun `setRegion updates region value`() = runTest {
        vm.setRegion("US")
        assertEquals("US", vm.region.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `removeCity calls deleteFavouriteCityUseCase`() = runTest {
        vm.removeCity("Madrid", "ES")

        advanceTimeBy(100)

        verify(deleteFavouriteCityUseCase).invoke("Madrid", "ES")
    }

    @Test
    fun `state loads favourite cities on initialization`() = runTest {
        val mockWeatherResults = listOf(
            sampleWeatherResult("Madrid"),
            sampleWeatherResult("Barcelona")
        )

        whenever(fetchFavouritesCitiesUseCase("metric")).thenReturn(flowOf(mockWeatherResults))

        val vmWithData = HomeViewModel(
            fetchFavouritesCitiesUseCase = fetchFavouritesCitiesUseCase,
            fetchSuggestionsForCityUseCase = fetchSuggestionsForCityUseCase,
            deleteFavouriteCityUseCase = deleteFavouriteCityUseCase,
            getCurrentRegionUseCase = getCurrentRegionUseCase
        )

        vmWithData.state.test {
            var result = awaitItem()
            while (result is Result.Loading) {
                result = awaitItem()
            }

            assertTrue("Expected Result.Success but got ${result::class.simpleName}", result is Result.Success)

            val cities = (result as Result.Success).data
            assertEquals(2, cities.size)
            val cityNames = cities.map { it.name }
            assertTrue("Should contain Madrid", cityNames.contains("Madrid"))
            assertTrue("Should contain Barcelona", cityNames.contains("Barcelona"))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `setTemperatureUnit triggers reload with new units`() = runTest {
        val celsiusCities = sampleWeatherResultList()
        val fahrenheitCities = sampleWeatherResultList()

        whenever(fetchFavouritesCitiesUseCase("metric")).thenReturn(flowOf(celsiusCities))
        whenever(fetchFavouritesCitiesUseCase("imperial")).thenReturn(flowOf(fahrenheitCities))

        val vmWithData = HomeViewModel(
            fetchFavouritesCitiesUseCase = fetchFavouritesCitiesUseCase,
            fetchSuggestionsForCityUseCase = fetchSuggestionsForCityUseCase,
            deleteFavouriteCityUseCase = deleteFavouriteCityUseCase,
            getCurrentRegionUseCase = getCurrentRegionUseCase
        )

        vmWithData.state.test {
            var initialResult = awaitItem()
            while (initialResult is Result.Loading) {
                initialResult = awaitItem()
            }
            assertTrue("Expected Result.Success initially", initialResult is Result.Success)

            vmWithData.setTemperatureUnit(TemperatureUnit.FAHRENHEIT)

            advanceTimeBy(100)

            verify(fetchFavouritesCitiesUseCase).invoke("imperial")
        }
    }

    @Test
    fun `state handles error from use case`() = runTest {
        val errorFlow = flow<List<WeatherResult>> {
            throw RuntimeException("Network error")
        }

        whenever(fetchFavouritesCitiesUseCase("metric")).thenReturn(errorFlow)

        val vmWithError = HomeViewModel(
            fetchFavouritesCitiesUseCase = fetchFavouritesCitiesUseCase,
            fetchSuggestionsForCityUseCase = fetchSuggestionsForCityUseCase,
            deleteFavouriteCityUseCase = deleteFavouriteCityUseCase,
            getCurrentRegionUseCase = getCurrentRegionUseCase
        )

        vmWithError.state.test {
            var result = awaitItem()
            while (result is Result.Loading) {
                result = awaitItem()
            }

            assertTrue(
                "Expected Result.Error or Result.Success with empty data but got ${result::class.simpleName}",
                result is Result.Error || (result is Result.Success && result.data.isEmpty())
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onCityQueryChanged with valid query triggers search after debounce`() = runTest {
        val mockSuggestions = sampleCityCoordinates("Madrid")
        whenever(fetchSuggestionsForCityUseCase("Madrid")).thenReturn(flowOf(mockSuggestions))

        vm.citySuggestions.test {
            assertEquals(emptyList<CityCoordinatesResponse>(), awaitItem())

            vm.onCityQueryChanged("Madrid")

            advanceTimeBy(300)

            assertEquals(mockSuggestions, awaitItem())

            verify(fetchSuggestionsForCityUseCase).invoke("Madrid")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onCityQueryChanged with short query does not trigger search`() = runTest {
        vm.citySuggestions.test {
            assertEquals(emptyList<CityCoordinatesResponse>(), awaitItem())

            vm.onCityQueryChanged("a")

            advanceTimeBy(300)

            expectNoEvents()

            verify(fetchSuggestionsForCityUseCase, never()).invoke(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onCityQueryChanged debounces multiple rapid calls`() = runTest {
        val mockSuggestions = sampleCityCoordinates("Madrid")
        whenever(fetchSuggestionsForCityUseCase("Madrid")).thenReturn(flowOf(mockSuggestions))

        vm.citySuggestions.test {
            assertEquals(emptyList<CityCoordinatesResponse>(), awaitItem())

            vm.onCityQueryChanged("Ma")
            vm.onCityQueryChanged("Mad")
            vm.onCityQueryChanged("Madr")
            vm.onCityQueryChanged("Madrid")

            advanceTimeBy(300)

            assertEquals(mockSuggestions, awaitItem())

            verify(fetchSuggestionsForCityUseCase).invoke("Madrid")
            verify(fetchSuggestionsForCityUseCase, never()).invoke("Ma")
            verify(fetchSuggestionsForCityUseCase, never()).invoke("Mad")
            verify(fetchSuggestionsForCityUseCase, never()).invoke("Madr")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `clearCitySuggestions clears suggestions`() = runTest {
        val mockSuggestions = sampleCityCoordinates("Madrid")
        whenever(fetchSuggestionsForCityUseCase("Madrid")).thenReturn(flowOf(mockSuggestions))

        vm.citySuggestions.test {
            assertEquals(emptyList<CityCoordinatesResponse>(), awaitItem())

            vm.onCityQueryChanged("Madrid")
            advanceTimeBy(300)
            assertEquals(mockSuggestions, awaitItem())

            vm.clearCitySuggestions()

            expectNoEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `requestLocationAndUpdateRegion updates region on success`() = runTest {
        whenever(getCurrentRegionUseCase()).thenReturn("US")

        vm.requestLocationAndUpdateRegion()

        advanceTimeBy(100)

        assertEquals("US", vm.region.value)
        verify(getCurrentRegionUseCase).invoke()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `requestLocationAndUpdateRegion uses default region on error`() = runTest {
        whenever(getCurrentRegionUseCase()).thenThrow(RuntimeException("Error"))

        vm.requestLocationAndUpdateRegion()

        advanceTimeBy(100)

        assertEquals("ES", vm.region.value)
        verify(getCurrentRegionUseCase).invoke()
    }

}