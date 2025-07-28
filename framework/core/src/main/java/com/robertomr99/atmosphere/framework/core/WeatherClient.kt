package com.robertomr99.atmosphere.framework.core

import android.util.Log
import com.robertomr99.atmosphere.framework.weather.network.WeatherService
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

internal class WeatherClient(private val apikey: String){

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor{ apiKeyAsQuery(it) }
        .build()

    val instance = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create<WeatherService>()


    private fun apiKeyAsQuery(chain: Interceptor.Chain): Response {
        val response = chain.proceed(
            chain
                .request()
                .newBuilder()
                .url(
                    chain.request().url
                        .newBuilder()
                        .addQueryParameter("appid", apikey)
                        .build()
                )
                .build()
        )

        response.body?.let { body ->
            val bodyString = body.string()
            Log.d("Weather JSON" ,"API Response: $bodyString")
            return response.newBuilder()
                .body(bodyString.toResponseBody(body.contentType()))
                .build()
        }

        return response
    }

}