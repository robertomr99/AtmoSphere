package com.robertomr99.atmosphere.data

import android.util.Log
import com.robertomr99.atmosphere.data.cityCoord.CityCoordinatesResponse
import com.robertomr99.atmosphere.data.datasource.WeatherDataSource
import com.robertomr99.atmosphere.data.datasource.local.DataStoreManager
import com.robertomr99.atmosphere.data.datasource.local.WeatherLocalDataSource
import com.robertomr99.atmosphere.data.datasource.local.toForecastResult
import com.robertomr99.atmosphere.data.datasource.local.toWeatherResult
import com.robertomr99.atmosphere.data.forecast.ForecastResult
import com.robertomr99.atmosphere.data.forecast.toEntityList
import com.robertomr99.atmosphere.data.weather.Sys
import com.robertomr99.atmosphere.data.weather.WeatherResult
import com.robertomr99.atmosphere.data.weather.toEntity
import com.robertomr99.atmosphere.ui.common.convertForecastFromKelvin
import com.robertomr99.atmosphere.ui.common.convertWeatherFromKelvin
import com.robertomr99.atmosphere.ui.common.normalizeForecastToKelvin
import com.robertomr99.atmosphere.ui.common.normalizeWeatherToKelvin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

const val FIFTEEN_MINUTES = 15 * 60 * 1000L
const val KELVIN = "standard"

data class NormalizedCity(
    val cityId: String,
    val displayName: String,
    val displayCountry: String
)

class WeatherRepository(
    private val regionRepository: RegionRepository,
    private val weatherDataSource: WeatherDataSource,
    private val weatherLocalDataSource: WeatherLocalDataSource,
    private val dataStoreManager: DataStoreManager
) {

    fun findIfCityIsFav(cityName: String, country: String): Flow<Int> {
        val normalized = normalizeCityData(cityName, country)
        return weatherLocalDataSource.findWeatherFavByCity(
            normalized.displayName,
            normalized.displayCountry
        ).transform {
            if (it != null) {
                emit(it)
            } else {
                emit(0)
            }
        }
    }

    suspend fun deleteFavouriteCity(cityName: String, country: String) {
        val normalized = normalizeCityData(cityName, country)
        weatherLocalDataSource.deleteCityByName(normalized.displayName, normalized.displayCountry)
        dataStoreManager.deleteTimestamp(
            city = normalized.displayName,
            country = normalized.displayCountry
        )
        dataStoreManager.deleteTimestamp(
            city = normalized.displayName,
            country = normalized.displayCountry,
            suffix = "_forecast"
        )
    }

    suspend fun saveFavouriteCity(
        weatherResult: Flow<WeatherResult>,
        forecastResult: Flow<ForecastResult>,
        temperatureUnit: String
    ) {
        try {
            Log.d("SAVE_FAV", "=== INICIANDO GUARDADO DE FAVORITA ===")

            val (weather, forecast) = withTimeout(10_000L) {
                combine(weatherResult, forecastResult) { w, f ->
                    Pair(w, f)
                }.first { (weather, forecast) ->
                    val isValid = weather.name != null &&
                            !forecast.list.isNullOrEmpty() &&
                            forecast.list.size == 40

                    Log.d("SAVE_FAV", "Validando datos - Weather: ${weather.name}, Forecast items: ${forecast.list?.size}, Válido: $isValid")
                    isValid
                }
            }

            Log.d("SAVE_FAV", "✅ Datos completos recibidos - Ciudad: ${weather.name}, Forecast items: ${forecast.list?.size}")

            val normalized = normalizeCityData(weather.name, weather.sys?.country)
            val normalizedWeather = normalizeWeatherToKelvin(weather, temperatureUnit)
            val normalizedForecast = normalizeForecastToKelvin(forecast, temperatureUnit)

            val weatherEntity = normalizedWeather.toEntity(cityId = normalized.cityId)
            val forecastEntities = normalizedForecast.toEntityList(cityOwnerId = normalized.cityId)

            Log.d("SAVE_FAV", "Guardando ${forecastEntities.size} entidades de forecast")

            weatherLocalDataSource.saveWeatherWithForecasts(weatherEntity, forecastEntities)

            val currentTime = System.currentTimeMillis()
            dataStoreManager.saveTimestamp(
                city = normalized.displayName,
                country = normalized.displayCountry,
                timestamp = currentTime
            )
            dataStoreManager.saveTimestamp(
                city = normalized.displayName,
                country = normalized.displayCountry,
                timestamp = currentTime,
                suffix = "_forecast"
            )

            Log.d("SAVE_FAV", "✅ Guardado completo exitoso: Weather + ${forecastEntities.size} forecasts + timestamps")

        } catch (e: TimeoutCancellationException) {
            Log.e("SAVE_FAV", "❌ Timeout esperando datos completos")
            throw Exception("No se pudieron obtener los datos completos de la ciudad")
        } catch (e: Exception) {
            Log.e("SAVE_FAV", "❌ Error guardando favorita: ${e.message}")
            throw e
        }
    }

    fun getWeatherForFavouritesCities(units: String): Flow<List<WeatherResult>> =
        weatherLocalDataSource.weatherList
            .distinctUntilChanged()
            .map { cities ->
                Log.d("HOME_UPDATE", "=== NUEVA EMISIÓN FAVORITAS: ${cities.size} ciudades ===")

                CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                    updateWeatherForCities(cities)
                }

                // ✅ Devolver datos actuales inmediatamente
                cities.map { city ->
                    convertWeatherFromKelvin(toWeatherResult(city), units)
                }
            }
            .flowOn(Dispatchers.IO)

    // ✅ Función separada para actualización en background
    private suspend fun updateWeatherForCities(cities: List<WeatherEntity>) {
        for (city in cities) {
            try {
                val cityName = city.name.substringBefore(",")
                val normalized = normalizeCityData(cityName, city.country)

                val needsUpdate = try {
                    !isWeatherCacheValid(normalized.displayName, normalized.displayCountry)
                } catch (e: Exception) {
                    false
                }

                if (needsUpdate) {
                    try {
                        Log.d("HOME_UPDATE", "Actualizando weather para: $cityName")

                        val remoteWeather = withTimeout(5_000L) {
                            val region = regionRepository.findLastRegion()
                            weatherDataSource.getWeatherForCity(
                                cityName = "$cityName,${city.country}",
                                units = KELVIN,
                                region = region
                            ).firstOrNull()
                        }

                        if (remoteWeather != null) {
                            val weatherWithCorrectName = remoteWeather.copy(name = cityName)

                            weatherLocalDataSource.saveWeather(
                                normalizeWeatherToKelvin(weatherWithCorrectName, KELVIN)
                                    .toEntity(cityId = normalized.cityId)
                            )

                            dataStoreManager.saveTimestamp(
                                city = normalized.displayName,
                                country = normalized.displayCountry,
                                timestamp = System.currentTimeMillis()
                            )

                            Log.d("HOME_UPDATE", "✅ Weather actualizado para: $cityName")
                        }
                    } catch (e: Exception) {
                        Log.e("HOME_UPDATE", "Error actualizando $cityName: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("HOME_UPDATE", "Error procesando ciudad ${city.name}: ${e.message}")
            }
        }
    }

    // 🔧 FIXED: DetailScreen con validación de datos completos
    fun getWeatherAndForecastForCity(
        city: String,
        country: String,
        units: String,
        isFavCity: Boolean
    ): Flow<Pair<WeatherResult?, ForecastResult?>> = channelFlow {
        val cityWithoutCountry = city.substringBefore(",").trim()

        if (isFavCity) {
            try {
                Log.d("DETAIL_GET", "=== OBTENIENDO DATOS DETALLE FAV: $cityWithoutCountry ===")

                val normalized = normalizeCityData(cityWithoutCountry, country)

                // ✅ Obtener datos locales
                val localData = try {
                    withTimeout(3_000L) {
                        weatherLocalDataSource.getWeatherWithForecastsByCity(
                            normalized.displayName,
                            normalized.displayCountry
                        ).firstOrNull()
                    }
                } catch (e: Exception) {
                    Log.w("DETAIL_GET", "Error leyendo datos locales: ${e.message}")
                    null
                }

                val forecastCacheValid = try {
                    isForecastCacheValid(normalized.displayName, normalized.displayCountry)
                } catch (e: Exception) {
                    false
                }

                val hasValidLocalForecast = (localData?.forecastList?.size ?: 0) == 40

                Log.d("DETAIL_GET", "Datos locales - Weather: ${localData?.weather?.name}, Forecasts: ${localData?.forecastList?.size}")
                Log.d("DETAIL_GET", "Cache válido: $forecastCacheValid, Forecast válido: $hasValidLocalForecast")

                val needsUpdate = localData == null || !hasValidLocalForecast || !forecastCacheValid

                if (needsUpdate) {
                    Log.d("DETAIL_GET", "🔄 ACTUALIZANDO datos desde remoto")

                    withContext(Dispatchers.IO + SupervisorJob()) {
                        try {
                            val (remoteWeather, remoteForecast) = withTimeout(10_000L) {
                                val region = regionRepository.findLastRegion()

                                val weatherDeferred = async {
                                    weatherDataSource.getWeatherForCity(
                                        "$cityWithoutCountry,$country",
                                        KELVIN,
                                        region
                                    ).firstOrNull()
                                }

                                val forecastDeferred = async {
                                    weatherDataSource.getForecastForCity(
                                        "$cityWithoutCountry,$country",
                                        KELVIN,
                                        region
                                    ).firstOrNull()
                                }

                                Pair(weatherDeferred.await(), forecastDeferred.await())
                            }

                            if (remoteWeather != null &&
                                remoteForecast != null &&
                                (remoteForecast.list?.size ?: 0) >= 40
                            ) {

                                Log.d("DETAIL_GET", "✅ Datos remotos válidos - Forecast: ${remoteForecast.list?.size} items")

                                val weatherWithCorrectName = remoteWeather.copy(name = cityWithoutCountry)
                                val normalizedWeather = normalizeWeatherToKelvin(weatherWithCorrectName, KELVIN)
                                val normalizedForecast = normalizeForecastToKelvin(remoteForecast, KELVIN)

                                val weatherEntity = normalizedWeather.toEntity(cityId = normalized.cityId)
                                val forecastEntities = normalizedForecast.toEntityList(cityOwnerId = normalized.cityId)

                                // ✅ Guardar en transacción
                                weatherLocalDataSource.saveWeatherWithForecasts(weatherEntity, forecastEntities)

                                val currentTime = System.currentTimeMillis()
                                dataStoreManager.saveTimestamp(
                                    city = normalized.displayName,
                                    country = normalized.displayCountry,
                                    timestamp = currentTime
                                )
                                dataStoreManager.saveTimestamp(
                                    city = normalized.displayName,
                                    country = normalized.displayCountry,
                                    timestamp = currentTime,
                                    suffix = "_forecast"
                                )

                                Log.d("DETAIL_GET", "✅ Actualización completa guardada")
                            } else {
                                Log.w("DETAIL_GET", "❌ Datos remotos incompletos - Weather: ${remoteWeather != null}, Forecast items: ${remoteForecast?.list?.size}")
                            }
                        } catch (e: Exception) {
                            Log.e("DETAIL_GET", "Error actualizando desde remoto: ${e.message}")
                        }
                    }
                }

                // ✅ Leer datos finales
                val finalData = try {
                    withTimeout(3_000L) {
                        weatherLocalDataSource.getWeatherWithForecastsByCity(
                            normalized.displayName,
                            normalized.displayCountry
                        ).firstOrNull()
                    }
                } catch (e: Exception) {
                    null
                }

                if (finalData != null && finalData.forecastList.isNotEmpty()) {
                    val weatherResult = convertWeatherFromKelvin(finalData.toWeatherResult(), units)
                    val forecastResult = convertForecastFromKelvin(finalData.toForecastResult(), units)

                    Log.d("DETAIL_GET", "✅ Enviando datos finales - Forecast: ${forecastResult.list?.size} items")
                    send(Pair(weatherResult, forecastResult))
                } else {
                    Log.e("DETAIL_GET", "❌ No hay datos finales válidos")
                    send(Pair(null, null))
                }

            } catch (e: Exception) {
                Log.e("DETAIL_GET", "❌ Error: ${e.message}")
                send(Pair(null, null))
            }
        } else {
            // Para ciudades NO favoritas
            try {
                Log.d("DETAIL_GET", "=== OBTENIENDO DATOS NO FAV: $city ===")

                val result = withContext(Dispatchers.IO) {
                    val region = regionRepository.findLastRegion()

                    val (remoteWeather, remoteForecast) = withTimeout(10_000L) {
                        val weatherDeferred = async {
                            weatherDataSource.getWeatherForCity(city, units, region)
                                .first { it?.name != null }
                        }

                        val forecastDeferred = async {
                            weatherDataSource.getForecastForCity(city, units, region)
                                .first { !it?.list.isNullOrEmpty() }
                        }

                        Pair(weatherDeferred.await(), forecastDeferred.await())
                    }

                    val correctedWeather = remoteWeather?.let { weather ->
                        if (weather.sys?.country.isNullOrEmpty()) {
                            weather.copy(
                                sys = weather.sys?.copy(country = country) ?: Sys(country = country)
                            )
                        } else {
                            weather
                        }
                    }

                    Log.d("DETAIL_GET", "Datos obtenidos - Weather: ${correctedWeather?.name}, Forecast: ${remoteForecast?.list?.size}")
                    Pair(correctedWeather, remoteForecast)
                }

                send(result)

            } catch (e: Exception) {
                Log.e("DETAIL_GET", "Error obteniendo datos no favoritos: ${e.message}")
                send(Pair(null, null))
            }
        }
    }

    fun getSuggestionsForCity(cityName: String): Flow<List<CityCoordinatesResponse>> {
        return flow {
            try {
                withTimeout(5_000L) {
                    val region = regionRepository.findLastRegion()
                    emitAll(weatherDataSource.getSuggestionsForCity(cityName, region))
                }
            } catch (e: Exception) {
                Log.e("SUGGESTIONS", "Error obteniendo sugerencias: ${e.message}")
                emit(emptyList())
            }
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun isWeatherCacheValid(cityName: String, country: String): Boolean {
        return try {
            val timestamp = dataStoreManager.getTimestamp(cityName, country)
            val now = System.currentTimeMillis()
            timestamp != null && (now - timestamp) < FIFTEEN_MINUTES
        } catch (e: Exception) {
            Log.e("CACHE_CHECK", "Error verificando cache weather: ${e.message}")
            false
        }
    }

    private suspend fun isForecastCacheValid(cityName: String, country: String): Boolean {
        return try {
            val timestamp = dataStoreManager.getTimestamp(cityName, country, "_forecast")
            val now = System.currentTimeMillis()
            timestamp != null && (now - timestamp) < FIFTEEN_MINUTES
        } catch (e: Exception) {
            Log.e("CACHE_CHECK", "Error verificando cache forecast: ${e.message}")
            false
        }
    }

    private fun normalizeCityData(cityName: String?, country: String?): NormalizedCity {
        val normalizedName = cityName?.trim()?.ifEmpty { null } ?: "Sin nombre"
        val normalizedCountry = country?.trim()?.ifEmpty { null } ?: "Sin comunidad"

        val cityId = "${normalizedName.lowercase()}_${normalizedCountry.lowercase()}"

        return NormalizedCity(
            cityId = cityId,
            displayName = normalizedName,
            displayCountry = normalizedCountry
        )
    }
}