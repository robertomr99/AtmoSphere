package com.robertomr99.atmosphere.data.datasource.remote

import android.util.Log
import com.robertomr99.atmosphere.data.cityCoord.CityCoordinatesResponse
import com.robertomr99.atmosphere.data.cityCoord.toRegionLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoCodingService {
    @GET("direct")
        suspend fun getCityCoordinates(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 5,
    ): List<CityCoordinatesResponse>
}

fun GeoCodingService.getFirstCityCoordinatesToRegionLanguage(
    cityName: String,
    region: String
): Flow<CityCoordinatesResponse?> = flow {
    try {
        val city = getCityCoordinates(cityName)
        emit(city.firstOrNull()?.toRegionLanguage(region))
    } catch (e: Exception) {
        Log.e("GeoCoding", "Error obteniendo coordenadas", e)
        emit(null)
    }
}

fun GeoCodingService.getListOfCitiesCoordinatesToRegionLanguage(
    cityName: String,
    region: String
): Flow<List<CityCoordinatesResponse>> = flow {
    try {
        val cityList = getCityCoordinates(cityName)
        emit(cityList.map { it.toRegionLanguage(region) })
    } catch (e: Exception) {
        Log.e("GeoCoding", "Error obteniendo coordenadas", e)
        emit(emptyList())
    }
}

