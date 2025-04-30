package com.robertomr99.atmosphere.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.robertomr99.atmosphere.R
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
    onClick: (String, String, String) -> Unit,
    vm: HomeViewModel = viewModel()
) {
    key(Unit) {
        val appName = stringResource(id = R.string.app_name)
        var appBarTitle by remember { mutableStateOf(appName) }
        val state by vm.state.collectAsState()
        val homeState = rememberHomeState()
        val errorMessage by NavigationState.cityError.collectAsState()

        homeState.ShowMessageEffect(errorMessage){
            NavigationState.clearCityError()
        }

        homeState.AskRegionEffect { region ->
            vm.setRegion(region)
            appBarTitle += " $region"
            vm.loadFavsCitiesWeather()
        }

        Screen {
            Scaffold(
                snackbarHost = {
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
                topBar = {
                    TopAppBar(
                        title = { Text(text = appBarTitle) },
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
                modifier = Modifier
                    .nestedScroll(homeState.scrollBehavior.nestedScrollConnection)
                    .background(Color.Transparent),
                contentWindowInsets = WindowInsets.safeDrawing,
                containerColor = Color.Transparent,
            ) { paddingValues ->
                var city by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(homeState.skyBlueGradient),
                ) {
                    Column(modifier = Modifier.padding(paddingValues)) {

                        Spacer(modifier = Modifier.height(8.dp))

                        SearchFieldWithVoice(
                            city = city,
                            onCityChange = { city = it },
                            onSearch = { query -> onClick(query, vm.region.value, unitsMapper(vm.temperatureUnit.value))},
                            errorMessage = errorMessage
                        )

                        if (state.loading) {
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

                        LazyColumn(modifier = Modifier.padding(16.dp)) {
                            items(
                                items = state.favCitiesWeatherResult,
                                key = { it.name }
                            ) { cityWeather ->

                                var visible by remember(cityWeather.name) { mutableStateOf(true) }

                                AnimatedVisibility(
                                    visible = visible,
                                    exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it }),
                                ) {
                                    LaunchedEffect(!visible) {
                                        if (!visible) {
                                            delay(300)
                                            vm.removeCity(cityWeather.name)
                                        }
                                    }

                                    WeatherCardWithImageAndGradient(
                                        cityWeather = cityWeather,
                                        onClick = {
                                            onClick(cityWeather.name, vm.region.value, unitsMapper(vm.temperatureUnit.value))
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

