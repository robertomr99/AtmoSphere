package com.robertomr99.atmosphere.ui.screens.home

import android.Manifest
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.robertomr99.atmosphere.ui.common.PermissionRequestEffect
import com.robertomr99.atmosphere.ui.common.getRegion
import kotlinx.coroutines.launch

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
    private var region by mutableStateOf("")

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

    @Composable
    fun AskRegionEffect(
        onRegion: (String) -> Unit
    ) {
        val ctx = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        PermissionRequestEffect(permission = Manifest.permission.ACCESS_COARSE_LOCATION) { granted ->
            coroutineScope.launch {
                region = if(granted)  ctx.getRegion() else "ES"
                onRegion(region)
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