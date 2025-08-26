package com.robertomr99.atmosphere.framework.core

import com.robertomr99.atmosphere.framework.weather.network.GeoCodingService
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

class GeoCodingClient(private val api: String, apiGeocodingUrl: String) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { apiKeyAsQuery(it) }
        .build()

    val instance = Retrofit.Builder()
        .baseUrl(apiGeocodingUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create<GeoCodingService>()

    private fun apiKeyAsQuery(chain: Interceptor.Chain) = chain.proceed(
        chain
            .request()
            .newBuilder()
            .url(
                chain.request().url
                    .newBuilder()
                    .addQueryParameter("appid", api)
                    .build()
            )
            .build()
    )
}
