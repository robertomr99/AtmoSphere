package com.robertomr99.atmosphere.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = WeatherEntity::class,
        parentColumns = ["cityId"],
        childColumns = ["cityOwnerId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityOwnerId: String,
    val hour: Int,
    val temp: Int,
    val tempMin: Int,
    val tempMax: Int,
    val weatherIcon: String
)

