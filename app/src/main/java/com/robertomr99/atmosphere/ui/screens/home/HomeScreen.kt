package com.robertomr99.atmosphere.ui.screens.home

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.robertomr99.atmosphere.R
import com.robertomr99.atmosphere.data.forecast.City
import com.robertomr99.atmosphere.ui.common.PermissionRequestEffect
import com.robertomr99.atmosphere.ui.common.getRegion
import com.robertomr99.atmosphere.ui.theme.AtmoSphereTheme
import kotlinx.coroutines.launch

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
    onClick: (String) -> Unit,
    vm: HomeViewModel = viewModel()
){
    val skyBlueGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF87CEEB),
            Color(0xFF4682B4)
        )
    )

    val ctx = LocalContext.current
    val appName = stringResource(id = R.string.app_name)
    var appBarTitle by remember { mutableStateOf(appName) }
    val coroutineScope = rememberCoroutineScope()
    val state = vm.state

    PermissionRequestEffect(permission = Manifest.permission.ACCESS_COARSE_LOCATION) { granted ->

        if(granted){
            coroutineScope.launch {
                val region = ctx.getRegion()
                appBarTitle = "$appName ($region)"
            }
        }else{
            appBarTitle = "$appName (Permission Denied)"
        }
        vm.onUiReady()
    }

    Screen {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = appBarTitle) },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.DarkGray.copy(alpha = 0.3f),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = Color.Transparent
        ){ paddingValues ->
            var city by remember { mutableStateOf("") }

           Column(
               modifier = Modifier
                   .fillMaxSize()
                   .background(skyBlueGradient),
           ){
            Column(modifier = Modifier.padding(paddingValues)) {

                Spacer(modifier = Modifier.height(8.dp))

                SearchFieldWithVoice(
                    city = city,
                    onCityChange = { city = it },
                    onSearch = { query ->
                        onClick(query)
                    }
                )

                if(state.loading){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }

                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(state.cities) { city ->
                            CityCard(city, onClick = {onClick(city.name!!)})
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                }
            }
        }
        }
    }

@Composable
fun CityCard(city: City?, onClick: () -> Unit){
    city?.let {
        Card(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = ShapeDefaults.ExtraLarge
                )
                .clip(ShapeDefaults.ExtraLarge),
            shape = ShapeDefaults.ExtraLarge,
            onClick = onClick
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = city.name!!,
                    color = Color.Black
                )
            }
        }
    }
}


