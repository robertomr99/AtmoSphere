package com.robertomr99.atmosphere.data.datasource.remote

import com.robertomr99.atmosphere.data.cityCoord.CityCoordinatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoCodingService {
    @GET("direct")
    suspend fun getCityCoordinates(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 5
    ): List<CityCoordinatesResponse>
}

suspend fun GeoCodingService.getFirstCityCoordinates(cityName: String): CityCoordinatesResponse? {
    return try {
        getCityCoordinates(cityName).firstOrNull()
    } catch (e: Exception) {
        null
    }
}