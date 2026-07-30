package ru.landilf.hellofbullets.domain.engine.player

import ru.landilf.hellofbullets.domain.model.player.PlayerExperienceProgress
import javax.inject.Inject

class PlayerProgressionCalculator @Inject constructor() {
    fun getRequiredExperienceForNextLevel(
        level: Int
    ): Int {
        require(level >= MIN_LEVEL) {
            "Уровень игрока должен быть не меньше $MIN_LEVEL"
        }

        val levelOffset = level - MIN_LEVEL

        return BASE_REQUIRED_EXPERIENCE + EXPERIENCE_GROWTH_FACTOR * levelOffset * levelOffset
    }

    fun calculateProgress(
        totalExperience: Int
    ): PlayerExperienceProgress {
        require(totalExperience >= 0) {
            "Общий опыт игрока не может быть отрицательным"
        }

        var remainingExperience = totalExperience
        var currentLevel = MIN_LEVEL

        while (remainingExperience >= getRequiredExperienceForNextLevel(currentLevel)) {
            remainingExperience -= getRequiredExperienceForNextLevel(currentLevel)
            currentLevel++
        }

        return PlayerExperienceProgress(
            level = currentLevel,
            experienceInCurrentLevel = remainingExperience,
            requiredExperienceForNextLevel = getRequiredExperienceForNextLevel(currentLevel)
        )
    }

    private companion object {
        const val MIN_LEVEL = 1
        const val BASE_REQUIRED_EXPERIENCE = 25
        const val EXPERIENCE_GROWTH_FACTOR = 15
    }
}