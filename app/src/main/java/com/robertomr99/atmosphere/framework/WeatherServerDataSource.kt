package com.robertomr99.atmosphere.framework

import com.robertomr99.atmosphere.domain.CityCoordinatesResponse
import com.robertomr99.atmosphere.domain.ForecastResult
import com.robertomr99.atmosphere.domain.WeatherResult
import com.robertomr99.atmosphere.framework.remote.GeoCodingService
import com.robertomr99.atmosphere.framework.remote.WeatherService
import com.robertomr99.atmosphere.framework.remote.getFirstCityCoordinatesToRegionLanguage
import com.robertomr99.atmosphere.framework.remote.getListOfCitiesCoordinatesToRegionLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

class WeatherServerDataSource(
    private val geoCodingService: GeoCodingService,
    private val weatherService: WeatherService
) : com.robertomr99.atmosphere.data.datasource.WeatherRemoteDataSource {

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