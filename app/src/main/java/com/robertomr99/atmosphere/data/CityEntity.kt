package com.robertomr99.atmosphere.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val favourite: Boolean = true
)