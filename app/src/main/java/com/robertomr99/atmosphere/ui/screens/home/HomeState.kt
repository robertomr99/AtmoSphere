package com.robertomr99.atmosphere.ui.screens.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

class HomeState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val scrollBehavior: TopAppBarScrollBehavior,
    val snackbarHostState: SnackbarHostState,
    val skyBlueGradient: Brush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4B4D4F),
            Color(0xFF000000)
        )
    )
) {

    var menuExpanded by mutableStateOf(false)
        private set

    var selectedTemperatureUnit by mutableStateOf(TemperatureUnit.CELSIUS)
        private set

    fun toggleMenu(expanded: Boolean) {
        menuExpanded = expanded
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        selectedTemperatureUnit = unit
    }

    @Composable
    fun ShowMessageEffect(message: String?, onMessageShow : () -> Unit){
        LaunchedEffect(message) {
            message?.let {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
                onMessageShow()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberHomeState(
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): HomeState {
    return remember(scrollBehavior, snackbarHostState) { HomeState(scrollBehavior, snackbarHostState) }
}