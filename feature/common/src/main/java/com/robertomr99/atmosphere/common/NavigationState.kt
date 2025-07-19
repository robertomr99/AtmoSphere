package com.robertomr99.atmosphere.common

import kotlinx.coroutines.flow.MutableStateFlow

object NavigationState {
    val cityError = MutableStateFlow<String?>(null)

    fun setCityError(errorMessage: String) {
        cityError.value = errorMessage
    }

    fun clearCityError() {
        cityError.value = null
    }
}