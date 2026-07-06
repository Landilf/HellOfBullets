package ru.landilf.hellofbullets.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import ru.landilf.hellofbullets.presentation.mainmenu.MainMenuUiState
import ru.landilf.hellofbullets.presentation.navigation.AppNavHost

@Composable
fun AppRoot(
    mainMenuState: MainMenuUiState,
    onExit: () -> Unit
) {
    val navController = rememberNavController()

    AppNavHost(
        navController = navController,
        mainMenuState = mainMenuState,
        onExit = onExit
    )
}