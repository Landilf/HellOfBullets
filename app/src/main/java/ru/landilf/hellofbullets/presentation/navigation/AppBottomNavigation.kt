package ru.landilf.hellofbullets.presentation.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.landilf.hellofbullets.R

@Composable
fun AppBottomNavigation(
    navController: NavHostController
) {
    val navigationItems = listOf(
        AppNavigationItem(
            destination = AppDestination.Shop,
            labelRes = R.string.main_menu_shop,
            icon = Icons.Outlined.Storefront
        ),
        AppNavigationItem(
            destination = AppDestination.Skills,
            labelRes = R.string.main_menu_skills,
            icon = Icons.Outlined.Psychology
        ),
        AppNavigationItem(
            destination = AppDestination.SelectMode,
            labelRes = R.string.main_menu_select_mode,
            icon = Icons.Outlined.PlayCircle
        ),
        AppNavigationItem(
            destination = AppDestination.Equipment,
            labelRes = R.string.main_menu_equipment,
            icon = Icons.Outlined.Inventory2
        ),
        AppNavigationItem(
            destination = AppDestination.Settings,
            labelRes = R.string.main_menu_settings,
            icon = Icons.Outlined.Settings
        )
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        navigationItems.forEach { item ->
            NavigationBarItem(
                selected = when (currentRoute) {
                    AppDestination.SelectMode.route,
                    AppDestination.SurvivalHome.route,
                    AppDestination.Duel.route -> {
                        item.destination == AppDestination.SelectMode
                    }

                    else -> currentRoute == item.destination.route
                },
                onClick = {
                    navController.navigate(item.destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.labelRes),
                        modifier = Modifier.size(28.dp)
                    )
                }
            )
        }
    }
}
