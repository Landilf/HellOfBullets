package ru.landilf.hellofbullets.domain.model.player

data class PlayerExperienceProgress(
    val level: Int,
    val experienceInCurrentLevel: Int,
    val requiredExperienceForNextLevel: Int
)