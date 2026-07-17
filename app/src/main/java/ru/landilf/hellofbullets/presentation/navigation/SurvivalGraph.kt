package ru.landilf.hellofbullets.presentation.navigation

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.presentation.common.PlaceholderScreen
import ru.landilf.hellofbullets.presentation.survival.SurvivalHomeScreen
import ru.landilf.hellofbullets.presentation.survival.game.SurvivalGameAction
import ru.landilf.hellofbullets.presentation.survival.game.SurvivalGameScreen
import ru.landilf.hellofbullets.presentation.survival.game.SurvivalGameViewModel

fun NavGraphBuilder.survivalGraph(
    navController: NavController
) {
    composable(AppDestination.SurvivalHome.route) {
        SurvivalHomeScreen(
            onStartGameClick = { navController.navigate(AppDestination.SurvivalGame.route) },
            onShowRecordsClick = { navController.navigate(AppDestination.SurvivalRecords.route) },
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(AppDestination.SurvivalGame.route) {
        val viewModel: SurvivalGameViewModel = hiltViewModel()
        val state = viewModel.uiState.collectAsState()

        SurvivalGameScreen(
            state = state.value,
            onAction = { action ->
                when (action) {
                    SurvivalGameAction.OnBackClick -> navController.popBackStack()
                    is SurvivalGameAction.OnPlayerDrag -> viewModel.onAction(action)
                    is SurvivalGameAction.OnGameFieldSizeChange -> viewModel.onAction(action)
                }
            }
        )
    }

    composable(AppDestination.SurvivalRecords.route) {
        PlaceholderScreen(
            titleRes = R.string.button_show_records,
            onBackClick = { navController.popBackStack() }
        )
    }
}