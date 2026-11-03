package ru.landilf.hellofbullets.domain.usecase.skills

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.landilf.hellofbullets.domain.fixtures.PlayerTestFixtures.createPlayerState
import ru.landilf.hellofbullets.domain.model.skills.SkillProgress
import ru.landilf.hellofbullets.domain.model.skills.SkillSlot
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository

class SetEquippedSkillUseCaseTest {
    @Test
    fun `equips unlocked skill and saves player state`() = runBlocking {
        val playerRepository = FakePlayerRepository(
            initialState = createPlayerState(
                skillProgresses = listOf(
                    SkillProgress(
                        skillId = 1L,
                        level = 1
                    )
                )
            )
        )
        val useCase = SetEquippedSkillUseCase(playerRepository)

        useCase(
            slot = SkillSlot.FIRST,
            skillId = 1L
        )

        assertEquals(
            1L,
            playerRepository.state?.playerBuild?.firstSkillId
        )
        assertEquals(
            null,
            playerRepository.state?.playerBuild?.secondSkillId
        )
    }

    @Test
    fun `clears selected skill slot and saves player state`() = runBlocking {
        val initialState = createPlayerState(
            firstSkillId = 1L,
            skillProgresses = listOf(
                SkillProgress(
                    skillId = 1L,
                    level = 1
                )
            )
        )
        val playerRepository = FakePlayerRepository(initialState)
        val useCase = SetEquippedSkillUseCase(playerRepository)

        useCase(
            slot = SkillSlot.FIRST,
            skillId = null
        )

        assertEquals(
            null,
            playerRepository.state?.playerBuild?.firstSkillId
        )
    }

    @Test
    fun `fails when equipping locked skill without changing state`() = runBlocking {
        val initialState = createPlayerState()
        val playerRepository = FakePlayerRepository(initialState)
        val useCase = SetEquippedSkillUseCase(playerRepository)

        val exception = runCatching {
            useCase(
                slot = SkillSlot.FIRST,
                skillId = 1L
            )
        }.exceptionOrNull()

        assertTrue(exception is IllegalArgumentException)
        assertEquals(initialState, playerRepository.state)
    }
}