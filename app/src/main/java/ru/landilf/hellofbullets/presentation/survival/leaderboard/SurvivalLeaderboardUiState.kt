package ru.landilf.hellofbullets.presentation.survival.leaderboard

import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord

data class SurvivalLeaderboardUiState(
    val isLoading: Boolean = true,
    val records: List<LeaderboardRecord> = emptyList(),
    val playerId: String = "",
    val errorMessage: String? = null
)
