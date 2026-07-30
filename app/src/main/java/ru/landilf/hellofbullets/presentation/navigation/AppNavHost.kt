package ru.landilf.hellofbullets.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.landilf.hellofbullets.presentation.profile.PlayerProfileOverlay
import ru.landilf.hellofbullets.presentation.profile.PlayerSummaryBar
import ru.landilf.hellofbullets.presentation.profile.PlayerSummaryViewModel

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val navigationRoutes = setOf(
        AppDestination.Shop.route,
        AppDestination.Skills.route,
        AppDestination.SelectMode.route,
        AppDestination.Equipment.route,
        AppDestination.Settings.route,
        AppDestination.SurvivalHome.route,
        AppDestination.Duel.route
    )

    val playerSummaryViewModel: PlayerSummaryViewModel = hiltViewModel()
    val playerSummaryState by playerSummaryViewModel.uiState.collectAsStateWithLifecycle()

    val profileVisibleState = rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                AnimatedVisibility(
                    visible = currentRoute in navigationRoutes,
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween(durationMillis = 200)
                    ) + fadeIn(animationSpec = tween(durationMillis = 150)),
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(durationMillis = 200)
                    ) + fadeOut(animationSpec = tween(durationMillis = 150))
                ) {
                    PlayerSummaryBar(
                        state = playerSummaryState,
                        onProfileClick = {
                            profileVisibleState.value = true
                        }
                    )
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = currentRoute in navigationRoutes,
                    enter = expandVertically(
                        expandFrom = Alignment.Bottom,
                        animationSpec = tween(durationMillis = 200)
                    ) + fadeIn(animationSpec = tween(durationMillis = 150)),
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Bottom,
                        animationSpec = tween(durationMillis = 200)
                    ) + fadeOut(animationSpec = tween(durationMillis = 150))
                ) {
                    AppBottomNavigation(navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppDestination.SelectMode.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                mainMenuGraph(navController = navController)
                survivalGraph(navController = navController)
            }
        }

        if (profileVisibleState.value) {
            PlayerProfileOverlay(
                state = playerSummaryState,
                onDismissRequest = {
                    profileVisibleState.value = false
                }
            )
        }
    }
}
