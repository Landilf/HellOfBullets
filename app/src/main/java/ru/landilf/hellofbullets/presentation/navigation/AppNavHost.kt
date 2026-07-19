package ru.landilf.hellofbullets.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import ru.landilf.hellofbullets.presentation.mainmenu.MainMenuUiState

@Composable
fun AppNavHost(
    navController: NavHostController,
    mainMenuUiState: MainMenuUiState,
    onExit: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.MainMenu.route
    ) {
        mainMenuGraph(
            navController = navController,
            mainMenuUiState = mainMenuUiState,
            onExit = onExit
        )

        survivalGraph(
            navController = navController
        )
    }
}