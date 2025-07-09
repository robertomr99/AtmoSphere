package com.robertomr99.atmosphere.domain.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rain(
    @SerialName("3h") val threeHours: Double? = null
)