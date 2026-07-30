package ru.landilf.hellofbullets.domain.engine.player

import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerProgressionCalculatorTest {
    private val calculator = PlayerProgressionCalculator()

    @Test
    fun `calculates quadratic experience requirements`() {
        assertEquals(
            25,
            calculator.getRequiredExperienceForNextLevel(level = 1)
        )
        assertEquals(
            40,
            calculator.getRequiredExperienceForNextLevel(level = 2)
        )
        assertEquals(
            85,
            calculator.getRequiredExperienceForNextLevel(level = 3)
        )
        assertEquals(
            160,
            calculator.getRequiredExperienceForNextLevel(level = 4)
        )
    }

    @Test
    fun `keeps first level before required experience is reached`() {
        val progress = calculator.calculateProgress(totalExperience = 24)

        assertEquals(1, progress.level)
        assertEquals(24, progress.experienceInCurrentLevel)
        assertEquals(25, progress.requiredExperienceForNextLevel)
    }

    @Test
    fun `increases level at exact experience threshold`() {
        val progress = calculator.calculateProgress(totalExperience = 25)

        assertEquals(2, progress.level)
        assertEquals(0, progress.experienceInCurrentLevel)
        assertEquals(40, progress.requiredExperienceForNextLevel)
    }

    @Test
    fun `calculates progress after multiple level increases`() {
        val progress = calculator.calculateProgress(totalExperience = 170)

        assertEquals(4, progress.level)
        assertEquals(20, progress.experienceInCurrentLevel)
        assertEquals(160, progress.requiredExperienceForNextLevel)
    }
}