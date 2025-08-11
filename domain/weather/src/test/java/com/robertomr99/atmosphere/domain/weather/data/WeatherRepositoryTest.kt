package com.robertomr99.atmosphere.domain.weather.data

import com.robertomr99.atmosphere.domain.region.data.RegionRepository
import com.robertomr99.atmosphere.domain.weather.IDataStoreManager
import com.robertomr99.atmosphere.domain.weather.Logger
import com.robertomr99.atmosphere.sampleCityCoordinatesResponse
import com.robertomr99.atmosphere.sampleForecastResult
import com.robertomr99.atmosphere.sampleForecastResultComplete
import com.robertomr99.atmosphere.sampleWeatherEntity
import com.robertomr99.atmosphere.sampleWeatherResult
import com.robertomr99.atmosphere.sampleWeatherResultComplete
import com.robertomr99.atmosphere.sampleWeatherWithForecasts
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class WeatherRepositoryTest {

    @Mock
    lateinit var regionRepository: RegionRepository
    @Mock
    lateinit var localDataSource: WeatherLocalDataSource
    @Mock
    lateinit var remoteDataSource: WeatherRemoteDataSource
    @Mock
    lateinit var dataStoreManager: IDataStoreManager
    @Mock
    lateinit var logger: Logger

    private lateinit var repository: WeatherRepository

    @Before
    fun setUp() {
        repository = WeatherRepository(
            regionRepository = regionRepository,
            weatherLocalDataSource = localDataSource,
            weatherRemoteDataSource = remoteDataSource,
            dataStoreManager = dataStoreManager,
            logger = logger
        )
    }

    @Test
    fun `getWeatherForFavouritesCities returns data from local source`(): Unit = runBlocking {
        // Given
        val units = "metric"
        val favoriteCities = listOf(
            sampleWeatherEntity("Madrid", "ES"),
            sampleWeatherEntity("Barcelona", "ES")
        )

        whenever(localDataSource.weatherList).thenReturn(flowOf(favoriteCities))

        // When
        val result = repository.getWeatherForFavouritesCities(units).first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Madrid", result[0].name)
        assertEquals("Barcelona", result[1].name)
        verify(localDataSource).weatherList
    }

    @Test
    fun `findIfCityIsFav returns 1 when city is favorite`(): Unit = runBlocking {
        // Given
        val cityName = "Sevilla"
        val country = "ES"

        whenever(
            localDataSource.findWeatherFavByCity(cityName, country)
        ).thenReturn(flowOf(1))

        // When
        val result = repository.findIfCityIsFav(cityName, country).first()

        // Then
        assertEquals(1, result)
        verify(localDataSource).findWeatherFavByCity(cityName, country)
    }

    @Test
    fun `findIfCityIsFav returns 0 when city is not favorite`(): Unit = runBlocking {
        // Given
        val cityName = "Valencia"
        val country = "ES"

        whenever(
            localDataSource.findWeatherFavByCity(cityName, country)
        ).thenReturn(flowOf(null))

        // When
        val result = repository.findIfCityIsFav(cityName, country).first()

        // Then
        assertEquals(0, result)
        verify(localDataSource).findWeatherFavByCity(cityName, country)
    }

    @Test
    fun `saveFavouriteCity saves weather and forecast to local source`(): Unit = runBlocking {
        // Given
        val weatherResult = flowOf(sampleWeatherResultComplete("Bilbao"))
        val forecastResult = flowOf(sampleForecastResultComplete())
        val units = "metric"

        // When
        repository.saveFavouriteCity(weatherResult, forecastResult, units)

        // Then
        verify(localDataSource).saveWeatherWithForecasts(any(), any())
        verify(dataStoreManager, times(2)).saveTimestamp(any(), any(), any(), any())
    }

    @Test
    fun `deleteFavouriteCity removes city from local source and clears timestamps`() = runBlocking {
        val cityName = "Toledo"
        val country = "ES"

        repository.deleteFavouriteCity(cityName, country)

        verify(localDataSource).deleteCityByName(cityName, country)
        verify(dataStoreManager).deleteTimestamp(cityName, country)
        verify(dataStoreManager).deleteTimestamp(cityName, country, "_forecast")
    }

    @Test
    fun `getWeatherAndForecastForCity returns local data when available and cache valid`(): Unit = runBlocking {
        val cityName = "Córdoba"
        val country = "ES"
        val units = "metric"
        val weatherWithForecasts = sampleWeatherWithForecasts(
            cityId = "córdoba_es",
            name = cityName
        )

        whenever(
            localDataSource.getWeatherWithForecastsByCity(cityName, country)
        ).thenReturn(flowOf(weatherWithForecasts))

        // Mock caché válido
        whenever(dataStoreManager.getTimestamp(cityName, country, "_forecast"))
            .thenReturn(System.currentTimeMillis())

        // When
        val result = repository.getWeatherAndForecastForCity(
            cityName, country, units, isFavCity = true
        ).first()

        // Then
        assertNotNull(result.first) // WeatherResult
        assertNotNull(result.second) // ForecastResult
        assertEquals(cityName, result.first?.name)

        // Verificar que se leyó de local y no de remoto
        verify(localDataSource, atLeastOnce()).getWeatherWithForecastsByCity(cityName, country)
        verify(remoteDataSource, never()).getWeatherForCity(any(), any(), any())
    }

    @Test
    fun `getWeatherAndForecastForCity fetches from remote for non-favorite cities`(): Unit = runBlocking {
        // Given
        val cityName = "Granada"
        val country = "ES"
        val units = "metric"

        whenever(regionRepository.findLastRegion()).thenReturn("ES")
        whenever(
            remoteDataSource.getWeatherForCity(cityName, units, "ES")
        ).thenReturn(flowOf(sampleWeatherResult(cityName)))

        whenever(
            remoteDataSource.getForecastForCity(cityName, units, "ES")
        ).thenReturn(flowOf(sampleForecastResult()))

        // When
        val result = repository.getWeatherAndForecastForCity(
            cityName, country, units, isFavCity = false
        ).first()

        // Then
        assertNotNull(result.first)
        assertNotNull(result.second)

        // Verificar que NO se usó local data source para ciudades no favoritas
        verify(localDataSource, never()).getWeatherWithForecastsByCity(any(), any())

        // Verificar que SÍ se usó remote data source
        verify(remoteDataSource).getWeatherForCity(cityName, units, "ES")
        verify(remoteDataSource).getForecastForCity(cityName, units, "ES")
    }

    @Test
    fun `getSuggestionsForCity returns suggestions from remote source`(): Unit = runBlocking {
        // Given
        val searchQuery = "Mad"
        val expectedSuggestions = listOf(
            sampleCityCoordinatesResponse("Madrid", "ES"),
            sampleCityCoordinatesResponse("Madeira", "PT"),
            sampleCityCoordinatesResponse("Madison", "US")
        )

        whenever(regionRepository.findLastRegion()).thenReturn("ES")
        whenever(
            remoteDataSource.getSuggestionsForCity(searchQuery, "ES")
        ).thenReturn(flowOf(expectedSuggestions))

        // When
        val result = repository.getSuggestionsForCity(searchQuery).first()

        // Then
        assertEquals(3, result.size)
        assertEquals("Madrid", result[0].name)
        assertEquals("Madeira", result[1].name)
        assertEquals("Madison", result[2].name)

        verify(regionRepository).findLastRegion()
        verify(remoteDataSource).getSuggestionsForCity(searchQuery, "ES")
    }

    @Test
    fun `getSuggestionsForCity returns empty list when remote fails`(): Unit = runBlocking {
        // Given
        val searchQuery = "XYZ"

        whenever(regionRepository.findLastRegion()).thenReturn("ES")
        whenever(
            remoteDataSource.getSuggestionsForCity(searchQuery, "ES")
        ).thenThrow(RuntimeException("Network error"))

        // When
        val result = repository.getSuggestionsForCity(searchQuery).first()

        // Then
        assertTrue(result.isEmpty())
        verify(remoteDataSource).getSuggestionsForCity(searchQuery, "ES")
    }



}