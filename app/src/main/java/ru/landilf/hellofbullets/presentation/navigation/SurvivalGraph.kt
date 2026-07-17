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
                    SurvivalGameAction.OnBackClick -> {
                        navController.popBackStack()
                    }

                    SurvivalGameAction.OnPauseClick,
                    SurvivalGameAction.OnResumeClick,
                    SurvivalGameAction.OnRestartClick,
                    is SurvivalGameAction.OnPlayerDrag,
                    is SurvivalGameAction.OnGameFieldSizeChange -> {
                        viewModel.onAction(action)
                    }

                    SurvivalGameAction.OnExitClick -> {
                        navController.popBackStack(AppDestination.SurvivalHome.route, false)
                    }
                }
            }
        )
    }

    composable(AppDestination.SurvivalRecords.route) {
        PlaceholderScreen(
            titleRes = R.string.show_records_button,
            onBackClick = { navController.popBackStack() }
        )
    }
}