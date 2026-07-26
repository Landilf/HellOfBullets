package ru.landilf.hellofbullets.presentation.survival.records

import ru.landilf.hellofbullets.domain.model.leaderboard.LeaderboardRecord

data class SurvivalRecordsUiState(
    val isLoading: Boolean = true,
    val records: List<LeaderboardRecord> = emptyList(),
    val playerName: String = "",
    val errorMessage: String? = null
)
