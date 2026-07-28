package ru.landilf.hellofbullets.presentation.navigation

sealed class AppDestination(val route: String) {
    object SelectMode : AppDestination("select_mode")
    object Skills : AppDestination("skills")
    object Equipment : AppDestination("equipment")
    object Shop : AppDestination("shop")
    object Settings : AppDestination("settings")

    object SurvivalHome : AppDestination("survival_home")
    object Duel : AppDestination("duel")

    object SurvivalGame : AppDestination("survival_game")
    object SurvivalLeaderboard : AppDestination("survival_leaderboard")
}