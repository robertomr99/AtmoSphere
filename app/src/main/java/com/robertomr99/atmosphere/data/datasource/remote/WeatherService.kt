package com.robertomr99.atmosphere.data.datasource.remote

import com.robertomr99.atmosphere.data.forecast.ForecastResult
import com.robertomr99.atmosphere.data.weather.WeatherResult
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather")
    suspend fun fetchWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): WeatherResult

    @GET("weather")
    suspend fun fetchWeatherUnitDefault(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String
    ): WeatherResult

    @GET("forecast")
    suspend fun fetchForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): ForecastResult

    @GET("forecast")
    suspend fun fetchForecastUnitDefault(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String
    ): ForecastResult
}