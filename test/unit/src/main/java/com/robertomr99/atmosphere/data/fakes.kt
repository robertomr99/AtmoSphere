package com.robertomr99.atmosphere.data

import androidx.datastore.preferences.core.Preferences
import com.robertomr99.atmosphere.domain.region.data.DEFAULT_REGION
import com.robertomr99.atmosphere.domain.region.data.RegionDataSource
import com.robertomr99.atmosphere.domain.region.data.RegionRepository
import com.robertomr99.atmosphere.domain.weather.IDataStoreManager
import com.robertomr99.atmosphere.domain.weather.Logger
import com.robertomr99.atmosphere.domain.weather.data.WeatherLocalDataSource
import com.robertomr99.atmosphere.domain.weather.data.WeatherRemoteDataSource
import com.robertomr99.atmosphere.domain.weather.data.WeatherRepository
import com.robertomr99.atmosphere.domain.weather.entities.CityCoordinatesResponse
import com.robertomr99.atmosphere.domain.weather.entities.ForecastEntity
import com.robertomr99.atmosphere.domain.weather.entities.ForecastResult
import com.robertomr99.atmosphere.domain.weather.entities.WeatherEntity
import com.robertomr99.atmosphere.domain.weather.entities.WeatherResult
import com.robertomr99.atmosphere.domain.weather.entities.WeatherWithForecasts
import com.robertomr99.atmosphere.sampleCityCoordinates
import com.robertomr99.atmosphere.sampleForecastResult
import com.robertomr99.atmosphere.sampleWeatherResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

fun buildWeatherRepositoryWith(
    localWeatherData: List<WeatherEntity> = emptyList(),
    remoteWeatherResult: WeatherResult? = null,
    remoteForecastResult: ForecastResult? = null,
    citySuggestions: List<CityCoordinatesResponse> = emptyList()
): WeatherRepository {
    val regionRepository = RegionRepository(FakeRegionDataSource())
    val localDataSource = FakeLocalDataSource().apply {
        localWeatherData.forEach { addWeather(it) }
    }
    val remoteDataSource = FakeRemoteDataSource().apply {
        remoteWeatherResult?.let { weatherResult = it }
        remoteForecastResult?.let { forecastResult = it }
        listOfSuggestions = citySuggestions
    }
    val fakeDataStoreManager = FakeDataStoreManager()
    val fakeLogger = FakeLogger()

    return WeatherRepository(
        regionRepository = regionRepository,
        weatherLocalDataSource = localDataSource,
        weatherRemoteDataSource = remoteDataSource,
        dataStoreManager = fakeDataStoreManager,
        logger = fakeLogger
    )
}

class FakeDataStoreManager : IDataStoreManager {
    private val timestamps = mutableMapOf<String, Long>()

    override fun keyForCity(city: String, country: String, suffix: String): Preferences.Key<Long> {
        return androidx.datastore.preferences.core.longPreferencesKey("${city}_${country}_${suffix}")
    }

    override suspend fun saveTimestamp(city: String, country: String, timestamp: Long, suffix: String) {
        val key = "${city}_${country}_${suffix}"
        timestamps[key] = timestamp
    }

    override suspend fun getTimestamp(city: String, country: String, suffix: String): Long? {
        val key = "${city}_${country}_${suffix}"
        return timestamps[key]
    }

    override suspend fun deleteTimestamp(city: String, country: String, suffix: String) {
        val key = "${city}_${country}_${suffix}"
        timestamps.remove(key)
    }
}

class FakeLogger : Logger {
    val logs = mutableListOf<String>()

    override fun v(tag: String, msg: String): Int {
        logs.add("V/$tag: $msg")
        return 0
    }

    override fun v(tag: String, msg: String, tr: Throwable?): Int {
        logs.add("V/$tag: $msg ${tr?.message ?: ""}")
        return 0
    }

    override fun d(tag: String, msg: String): Int {
        logs.add("D/$tag: $msg")
        return 0
    }

    override fun d(tag: String, msg: String, tr: Throwable?): Int {
        logs.add("D/$tag: $msg ${tr?.message ?: ""}")
        return 0
    }

    override fun i(tag: String, msg: String): Int {
        logs.add("I/$tag: $msg")
        return 0
    }

    override fun i(tag: String, msg: String, tr: Throwable?): Int {
        logs.add("I/$tag: $msg ${tr?.message ?: ""}")
        return 0
    }

    override fun w(tag: String, msg: String): Int {
        logs.add("W/$tag: $msg")
        return 0
    }

    override fun w(tag: String, msg: String, tr: Throwable?): Int {
        logs.add("W/$tag: $msg ${tr?.message ?: ""}")
        return 0
    }

    override fun w(tag: String, tr: Throwable): Int {
        logs.add("W/$tag: ${tr.message}")
        return 0
    }

    override fun e(tag: String, msg: String): Int {
        logs.add("E/$tag: $msg")
        return 0
    }

    override fun e(tag: String, msg: String, tr: Throwable?): Int {
        logs.add("E/$tag: $msg ${tr?.message ?: ""}")
        return 0
    }

    override fun wtf(tag: String, msg: String): Int {
        logs.add("WTF/$tag: $msg")
        return 0
    }

    override fun wtf(tag: String, msg: String, tr: Throwable?): Int {
        logs.add("WTF/$tag: $msg ${tr?.message ?: ""}")
        return 0
    }

    override fun wtf(tag: String, tr: Throwable): Int {
        logs.add("WTF/$tag: ${tr.message}")
        return 0
    }
}


class FakeRegionDataSource : RegionDataSource {
    val region = DEFAULT_REGION
    override suspend fun findLastRegion(): String = region
}

class FakeLocalDataSource : WeatherLocalDataSource {

    private val inMemoryWeather = MutableStateFlow<List<WeatherEntity>>(emptyList())
    private val inMemoryForecasts = MutableStateFlow<Map<String, List<ForecastEntity>>>(emptyMap())

    override val weatherList: Flow<List<WeatherEntity>> = inMemoryWeather

    override fun findWeatherFavByCity(cityName: String, country: String): Flow<Int?> {
        return inMemoryWeather.map { weatherList ->
            weatherList.firstOrNull { weather ->
                weather.name == cityName && weather.country == country
            }?.let { 1 }
        }
    }

    override fun getWeatherWithForecastsByCity(
        cityName: String,
        country: String
    ): Flow<WeatherWithForecasts?> {
        return inMemoryWeather.map { weatherList ->
            val weather = weatherList.firstOrNull {
                it.name == cityName && it.country == country
            }

            weather?.let { weatherEntity ->
                val forecasts = inMemoryForecasts.value["${cityName}_${country}"] ?: emptyList()
                WeatherWithForecasts(
                    weather = weatherEntity,
                    forecastList = forecasts
                )
            }
        }
    }

    override suspend fun saveWeatherWithForecasts(
        weatherEntity: WeatherEntity,
        forecastList: List<ForecastEntity>
    ) {
        val currentWeatherList = inMemoryWeather.value.toMutableList()
        val existingIndex = currentWeatherList.indexOfFirst {
            it.name == weatherEntity.name && it.country == weatherEntity.country
        }

        if (existingIndex >= 0) {
            currentWeatherList[existingIndex] = weatherEntity
        } else {
            currentWeatherList.add(weatherEntity)
        }

        inMemoryWeather.value = currentWeatherList

        val currentForecasts = inMemoryForecasts.value.toMutableMap()
        val key = "${weatherEntity.name}_${weatherEntity.country}"
        currentForecasts[key] = forecastList
        inMemoryForecasts.value = currentForecasts
    }

    override suspend fun saveWeather(weatherEntity: WeatherEntity) {
        val currentList = inMemoryWeather.value.toMutableList()
        val existingIndex = currentList.indexOfFirst {
            it.name == weatherEntity.name && it.country == weatherEntity.country
        }

        if (existingIndex >= 0) {
            currentList[existingIndex] = weatherEntity
        } else {
            currentList.add(weatherEntity)
        }

        inMemoryWeather.value = currentList
    }

    override suspend fun deleteCityByName(cityName: String, country: String) {
        val currentWeatherList = inMemoryWeather.value.toMutableList()
        currentWeatherList.removeAll { it.name == cityName && it.country == country }
        inMemoryWeather.value = currentWeatherList

        val currentForecasts = inMemoryForecasts.value.toMutableMap()
        val key = "${cityName}_${country}"
        currentForecasts.remove(key)
        inMemoryForecasts.value = currentForecasts
    }

    fun addWeather(weatherEntity: WeatherEntity) {
        val currentList = inMemoryWeather.value.toMutableList()
        currentList.add(weatherEntity)
        inMemoryWeather.value = currentList
    }
}

class FakeRemoteDataSource : WeatherRemoteDataSource {
    var weatherResult = sampleWeatherResult("Madrid")
    var forecastResult = sampleForecastResult()
    var listOfSuggestions = sampleCityCoordinates("Madrid")


    override fun getWeatherForCity(
        cityName: String,
        units: String,
        region: String
    ): Flow<WeatherResult?> = flowOf(weatherResult)

    override fun getForecastForCity(
        cityName: String,
        units: String,
        region: String
    ): Flow<ForecastResult?> = flowOf(forecastResult)


    override fun getSuggestionsForCity(
        cityName: String,
        region: String
    ): Flow<List<CityCoordinatesResponse>> = flowOf(listOfSuggestions)
}