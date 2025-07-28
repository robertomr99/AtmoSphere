package com.robertomr99.atmosphere.framework.weather.database

import androidx.room.Embedded
import androidx.room.Relation

data class DbWeatherWithForecastsEntity(
    @Embedded val weather: DbWeatherEntity,
    @Relation(
        parentColumn = "cityId",
        entityColumn = "cityOwnerId"
    )
    val forecastList: List<DbForecastEntity>,
)