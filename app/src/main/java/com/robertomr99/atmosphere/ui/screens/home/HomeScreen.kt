package com.robertomr99.atmosphere.ui.screens.home

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.robertomr99.atmosphere.R
import com.robertomr99.atmosphere.ui.common.AcScaffold
import com.robertomr99.atmosphere.ui.common.PermissionRequestEffect
import com.robertomr99.atmosphere.ui.common.unitsMapper
import com.robertomr99.atmosphere.ui.screens.NavigationState
import com.robertomr99.atmosphere.ui.theme.AtmoSphereTheme
import kotlinx.coroutines.delay

@Composable
fun Screen(content: @Composable () -> Unit) {
    AtmoSphereTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onClick: (String, String) -> Unit,
    vm: HomeViewModel = viewModel()
) {
    key(Unit) {
        val appName = stringResource(id = R.string.app_name)
        val appBarTitle by remember { mutableStateOf("$appName ${vm.region.value}") }
        val state by vm.state.collectAsState()
        val temperatureUnit by vm.temperatureUnit.collectAsState()
        val homeState = rememberHomeState(temperatureUnit = temperatureUnit)
        val errorMessage by NavigationState.cityError.collectAsState()
        val citySuggestions by vm.citySuggestions.collectAsState()

        homeState.ShowMessageEffect(errorMessage) {
            NavigationState.clearCityError()
        }

        PermissionRequestEffect(permission = Manifest.permission.ACCESS_COARSE_LOCATION) {
            vm.loadFavsCitiesWeather()
        }

        Screen {
            AcScaffold(
                state = state,
                modifier = Modifier
                    .nestedScroll(homeState.scrollBehavior.nestedScrollConnection)
                    .background(Color.Transparent),
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.atmosphere),
                                    contentDescription = "AtmoSphere Logo",
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                )
                                Text(
                                    text = appBarTitle,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        },
                        scrollBehavior = homeState.scrollBehavior,
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White,
                            actionIconContentColor = Color.White
                        ),
                        actions = {
                            TopBarHomeActions(
                                homeState,
                                onUnitSelected = {
                                    vm.setTemperatureUnit(it)
                                }
                            )
                        }
                    )
                },
                snackBarHost = {
                    SnackbarHost(
                        hostState = homeState.snackbarHostState,
                        snackbar = {
                            Snackbar(
                                snackbarData = it,
                                containerColor = Color.Red,
                                contentColor = Color.White,
                            )
                        }
                    )
                },
                contentWindowInsets = WindowInsets.safeDrawing,
                loadingContent = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(homeState.skyBlueGradient)
                            .padding(paddingValues)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(56.dp)
                                .background(
                                    Color.White.copy(alpha = 0.1f),
                                    androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                )
                        )

                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            repeat(6) {
                                ShimmerCityCard()
                            }
                        }
                    }
                },
                errorContent = { paddingValues, errorMessage ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(homeState.skyBlueGradient)
                            .padding(paddingValues)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Error al cargar las ciudades favoritas: $errorMessage",
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Toca para reintentar",
                                    color = Color.White.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            ) { paddingValues, favCitiesWeather ->
                var city by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(homeState.skyBlueGradient)
                        .padding(paddingValues)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    SearchFieldWithVoice(
                        city = city,
                        onCityChange = {
                            city = it
                            vm.onCityQueryChanged(it)
                        },
                        onSearch = { suggestion ->
                            city = "${suggestion.name}, ${suggestion.country}"
                            onClick(city, unitsMapper(vm.temperatureUnit.value))
                            vm.clearCitySuggestions()
                        },
                        suggestions = citySuggestions,
                        errorMessage = errorMessage
                    )

                    if (favCitiesWeather.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No tienes ciudades favoritas aún.\nBusca una ciudad para agregarla.",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.padding(16.dp)) {
                            items(
                                items = favCitiesWeather,
                                key = { "${it.name}_${it.country}" }
                            ) { cityWeather ->

                                var visible by remember(cityWeather.name) { mutableStateOf(true) }

                                AnimatedVisibility(
                                    visible = visible,
                                    exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it }),
                                ) {
                                    LaunchedEffect(!visible) {
                                        if (!visible) {
                                            delay(300)
                                            vm.removeCity(cityWeather.name, cityWeather.country)
                                        }
                                    }

                                    WeatherCardWithImageAndGradient(
                                        cityWeather = cityWeather,
                                        onClick = {
                                            onClick(
                                                "${cityWeather.name},${cityWeather.country}",
                                                unitsMapper(vm.temperatureUnit.value)
                                            )
                                        },
                                        onDelete = { visible = false },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}