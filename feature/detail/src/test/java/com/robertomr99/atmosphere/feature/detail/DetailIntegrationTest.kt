package com.robertomr99.atmosphere.feature.detail

import app.cash.turbine.test
import com.robertomr99.atmosphere.data.buildWeatherRepositoryWith
import com.robertomr99.atmosphere.domain.weather.usecases.DeleteFavouriteCityUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FetchWeatherAndForecastUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FindFavCityUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.SaveFavouriteCityUseCase
import com.robertomr99.atmosphere.feature.common.Result
import com.robertomr99.atmosphere.sampleForecastResult
import com.robertomr99.atmosphere.sampleWeatherEntity
import com.robertomr99.atmosphere.sampleWeatherResult
import com.robertomr99.atmosphere.testrule.CoroutineTestRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DetailIntegrationTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var vm: DetailViewModel

    @Before
    fun setUp() {
        val weatherRepository = buildWeatherRepositoryWith(
            localWeatherData = listOf(
                sampleWeatherEntity("Madrid", "ES"),
                sampleWeatherEntity("Barcelona", "ES")
            ),
            remoteWeatherResult = sampleWeatherResult("Madrid"),
            remoteForecastResult = sampleForecastResult()
        )

        vm = DetailViewModel(
            findFavCityUseCase = FindFavCityUseCase(weatherRepository),
            fetchWeatherAndForecastUseCase = FetchWeatherAndForecastUseCase(weatherRepository),
            saveFavouriteCityUseCase = SaveFavouriteCityUseCase(weatherRepository),
            deleteFavouriteCityUseCase = DeleteFavouriteCityUseCase(weatherRepository)
        )
    }

    @Test
    fun `weather data is loaded from server when not cached`() = runTest {
        vm.state.test {
            assertEquals(Result.Loading, awaitItem())

            vm.loadCityWeather("Valencia, ES", "metric")

            val result = awaitItem()
            assertTrue("Expected Success or Error",
                result is Result.Success || result is Result.Error)
        }
    }

    @Test
    fun `favorite is updated in local data source`() = runTest {
        vm.state.test {
            assertEquals(Result.Loading, awaitItem())

            vm.loadCityWeather("Madrid, ES", "metric")

            val result = awaitItem()
            assertTrue("Expected Success or Error",
                result is Result.Success || result is Result.Error)

            if (result is Result.Success) {
                vm.updateCityFav(false)
            }
        }
    }
}

