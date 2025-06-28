package com.robertomr99.atmosphere.data.datasource

import com.robertomr99.atmosphere.data.cityCoord.CityCoordinatesResponse
import com.robertomr99.atmosphere.data.datasource.remote.GeoCodingClient
import com.robertomr99.atmosphere.data.datasource.remote.WeatherClient
import com.robertomr99.atmosphere.data.datasource.remote.getFirstCityCoordinatesToRegionLanguage
import com.robertomr99.atmosphere.data.datasource.remote.getListOfCitiesCoordinatesToRegionLanguage
import com.robertomr99.atmosphere.data.forecast.ForecastResult
import com.robertomr99.atmosphere.data.weather.WeatherResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

class WeatherDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getWeatherForCity(cityName: String, units: String, region: String): Flow<WeatherResult?> =
        GeoCodingClient.instance.getFirstCityCoordinatesToRegionLanguage(cityName, region)
            .flatMapLatest { coordinates ->
                if (coordinates == null) {
                    flowOf(null)
                } else {
                    flow {
                        val weather = if (units.isEmpty()) {
                            WeatherClient.instance.fetchWeatherUnitDefault(
                                lat = coordinates.lat,
                                lon = coordinates.lon,
                                lang = region
                            )
                        } else {
                            WeatherClient.instance.fetchWeather(
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
    fun getForecastForCity(cityName: String, units: String, region: String): Flow<ForecastResult?> =
        GeoCodingClient.instance.getFirstCityCoordinatesToRegionLanguage(cityName, region)
            .flatMapLatest { coordinates ->
                if (coordinates == null) {
                    flowOf(null)
                } else {
                    flow {
                        val forecast = if (units.isEmpty()) {
                            WeatherClient.instance.fetchForecastUnitDefault(
                                lat = coordinates.lat,
                                lon = coordinates.lon,
                                lang = region
                            )
                        } else {
                            WeatherClient.instance.fetchForecast(
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


    fun getSuggestionsForCity(cityName: String, region: String): Flow<List<CityCoordinatesResponse>> {
        return GeoCodingClient.instance.getListOfCitiesCoordinatesToRegionLanguage(cityName, region)
    }

}