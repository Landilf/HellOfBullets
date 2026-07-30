package ru.landilf.hellofbullets.presentation.profile

data class PlayerSummaryUiState(
    val isLoading: Boolean = true,
    val playerName: String = "",
    val level: Int = 1,
    val experienceInCurrentLevel: Int = 0,
    val requiredExperienceForNextLevel: Int = 0,
    val silverAmount: Int = 0
)