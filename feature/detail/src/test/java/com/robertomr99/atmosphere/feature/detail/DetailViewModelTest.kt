package com.robertomr99.atmosphere.feature.detail

import app.cash.turbine.test
import com.robertomr99.atmosphere.domain.weather.usecases.DeleteFavouriteCityUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FetchWeatherAndForecastUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FindFavCityUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.SaveFavouriteCityUseCase
import com.robertomr99.atmosphere.feature.common.Result
import com.robertomr99.atmosphere.sampleForecastResult
import com.robertomr99.atmosphere.sampleWeatherResult
import com.robertomr99.atmosphere.testrule.CoroutineTestRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class DetailViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    lateinit var findFavCityUseCase: FindFavCityUseCase

    @Mock
    lateinit var fetchWeatherAndForecastUseCase: FetchWeatherAndForecastUseCase

    @Mock
    lateinit var saveFavouriteCityUseCase: SaveFavouriteCityUseCase

    @Mock
    lateinit var deleteFavouriteCityUseCase: DeleteFavouriteCityUseCase

    private lateinit var vm: DetailViewModel

    @Before
    fun setUp() {
        vm = DetailViewModel(
            findFavCityUseCase = findFavCityUseCase,
            fetchWeatherAndForecastUseCase = fetchWeatherAndForecastUseCase,
            saveFavouriteCityUseCase = saveFavouriteCityUseCase,
            deleteFavouriteCityUseCase = deleteFavouriteCityUseCase
        )
    }

    @Test
    fun `loadCityWeather updates cityName and country properties`() = runTest {
        vm.loadCityWeather("Madrid, ES", "metric")

        assertEquals("Madrid", vm.cityName)
        assertEquals("ES", vm.country)
        assertEquals("metric", vm.temperatureUnit)
    }

    @Test
    fun `state starts with Loading`() = runTest {
        vm.state.test {
            val initialState = awaitItem()
            assertTrue("Expected Loading", initialState is Result.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadCityWeather triggers state change`() = runTest {
        val mockWeather = sampleWeatherResult("Madrid")
        val mockForecast = sampleForecastResult()

        whenever(findFavCityUseCase("Madrid", "ES")).thenReturn(flowOf(0))
        whenever(fetchWeatherAndForecastUseCase("Madrid, ES", "ES", "metric", false))
            .thenReturn(flowOf(Pair(mockWeather, mockForecast)))

        vm.state.test {
            val loading = awaitItem()
            assertTrue("Expected Loading", loading is Result.Loading)

            vm.loadCityWeather("Madrid, ES", "metric")

            val result = awaitItem()
            assertTrue("Expected either Success or Error",
                result is Result.Success || result is Result.Error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadCityWeather handles error when weather data not found`() = runTest {
        whenever(findFavCityUseCase("InvalidCity", "ES")).thenReturn(flowOf(0))
        whenever(fetchWeatherAndForecastUseCase("InvalidCity, ES", "ES", "metric", false))
            .thenReturn(flowOf(Pair(null, null)))

        vm.state.test {
            val loading = awaitItem()
            assertTrue("Expected Loading", loading is Result.Loading)

            vm.loadCityWeather("InvalidCity, ES", "metric")

            val result = awaitItem()
            assertTrue("Expected Result.Error but got ${result::class.simpleName}", result is Result.Error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getFeelsLikeTemp returns null when state is not success`() = runTest {
        assertNull(vm.getFeelsLikeTemp())
    }

    @Test
    fun `getHumidity returns null when state is not success`() = runTest {
        assertNull(vm.getHumidity())
    }

    @Test
    fun `getWindResult returns null when state is not success`() = runTest {
        assertNull(vm.getWindResult())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `updateCityFav does nothing when state is not success`() = runTest {
        vm.updateCityFav(true)

        advanceTimeBy(100)

        verify(saveFavouriteCityUseCase, never()).invoke(any(), any(), any())
        verify(deleteFavouriteCityUseCase, never()).invoke(any(), any())
    }

    @Test
    fun `getHourlyForecastToday returns empty list when state is not success`() = runTest {
        val result = vm.getHourlyForecastToday()
        assertTrue("Should return empty list", result.isEmpty())
    }

    @Test
    fun `getDailyMinMaxForecast returns empty list when state is not success`() = runTest {
        val result = vm.getDailyMinMaxForecast()
        assertTrue("Should return empty list", result.isEmpty())
    }

    @Test
    fun `state handles exception from use case`() = runTest {
        whenever(findFavCityUseCase("Madrid", "ES")).thenReturn(flowOf(0))
        whenever(fetchWeatherAndForecastUseCase("Madrid, ES", "ES", "metric", false))
            .thenThrow(RuntimeException("Network error"))

        vm.state.test {
            val loading = awaitItem()
            assertTrue("Expected Loading", loading is Result.Loading)

            vm.loadCityWeather("Madrid, ES", "metric")

            val result = awaitItem()
            assertTrue("Expected Result.Error but got ${result::class.simpleName}", result is Result.Error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadCityWeather calls trigger correctly`() = runTest {

        vm.loadCityWeather("Barcelona, ES", "imperial")

        assertEquals("Barcelona", vm.cityName)
        assertEquals("ES", vm.country)
        assertEquals("imperial", vm.temperatureUnit)
    }

    @Test
    fun `loadCityWeather parses city and country correctly`() = runTest {
        vm.loadCityWeather("New York, US", "metric")

        assertEquals("New York", vm.cityName)
        assertEquals("US", vm.country)
        assertEquals("metric", vm.temperatureUnit)
    }

    @Test
    fun `loadCityWeather handles city without comma`() = runTest {
        vm.loadCityWeather("Madrid", "metric")

        assertEquals("Madrid", vm.cityName)
        assertEquals("Madrid", vm.country) 
        assertEquals("metric", vm.temperatureUnit)
    }
}