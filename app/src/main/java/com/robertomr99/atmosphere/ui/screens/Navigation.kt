package com.robertomr99.atmosphere.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.robertomr99.atmosphere.ui.screens.detail.DetailScreen
import com.robertomr99.atmosphere.ui.screens.home.HomeScreen

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun Navigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home"){
        composable("home"){
            HomeScreen(onClick = { city ->
                navController.navigate("detail/$city")
            })
        }

        composable("detail/{city}",
            arguments = listOf(navArgument("city") {type = NavType.StringType})
        ){ backStackEntry ->
            val city = backStackEntry.arguments?.getString("city")
            DetailScreen(
                cityName = city!!,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}