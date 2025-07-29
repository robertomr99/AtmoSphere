package com.robertomr99.atmosphere.framework.weather.network

import com.robertomr99.atmosphere.domain.weather.data.WeatherRemoteDataSource
import com.robertomr99.atmosphere.domain.weather.entities.CityCoordinatesResponse
import com.robertomr99.atmosphere.domain.weather.entities.ForecastResult
import com.robertomr99.atmosphere.domain.weather.entities.WeatherResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class WeatherServerDataSource @Inject constructor(
    private val geoCodingService: GeoCodingService,
    private val weatherService: WeatherService
) : WeatherRemoteDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getWeatherForCity(cityName: String, units: String, region: String): Flow<WeatherResult?> =
        geoCodingService.getFirstCityCoordinatesToRegionLanguage(cityName, region)
            .flatMapLatest { coordinates ->
                if (coordinates == null) {
                    flowOf(null)
                } else {
                    flow {
                        val weather = if (units.isEmpty()) {
                            weatherService.fetchWeatherUnitDefault(
                                lat = coordinates.lat,
                                lon = coordinates.lon,
                                lang = region
                            )
                        } else {
                            weatherService.fetchWeather(
                                lat = coordinates.lat,
                                lon = coordinates.lon,
                                units = units,
                                lang = region
                            )
                        }
                        emit(weather)
                    }.flowOn(Dispatchers.IO)
                }
            }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getForecastForCity(cityName: String, units: String, region: String): Flow<ForecastResult?> =
        geoCodingService.getFirstCityCoordinatesToRegionLanguage(cityName, region)
            .flatMapLatest { coordinates ->
                if (coordinates == null) {
                    flowOf(null)
                } else {
                    flow {
                        val forecast = if (units.isEmpty()) {
                            weatherService.fetchForecastUnitDefault(
                                lat = coordinates.lat,
                                lon = coordinates.lon,
                                lang = region
                            )
                        } else {
                            weatherService.fetchForecast(
                                lat = coordinates.lat,
                                lon = coordinates.lon,
                                units = units,
                                lang = region
                            )
                        }
                        emit(forecast)
                    }.flowOn(Dispatchers.IO)
                }
            }


    override fun getSuggestionsForCity(cityName: String, region: String): Flow<List<CityCoordinatesResponse>> {
        return geoCodingService.getListOfCitiesCoordinatesToRegionLanguage(cityName, region)
    }

}