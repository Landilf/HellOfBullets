package ru.landilf.hellofbullets.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import ru.landilf.hellofbullets.presentation.navigation.AppNavHost

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    AppNavHost(
        navController = navController
    )
}