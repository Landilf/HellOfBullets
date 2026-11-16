package ru.landilf.hellofbullets.domain.usecase.skills

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.landilf.hellofbullets.domain.fixtures.PlayerTestFixtures.createPlayerState
import ru.landilf.hellofbullets.domain.model.skills.SkillProgress
import ru.landilf.hellofbullets.domain.model.skills.definition.ActiveSkillLevelParameters
import ru.landilf.hellofbullets.domain.model.skills.definition.ShieldSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.SkillDefinition
import ru.landilf.hellofbullets.domain.usecase.FakePlayerRepository
import ru.landilf.hellofbullets.domain.usecase.FakeSkillDefinitionRepository

class UnlockPlayerSkillUseCaseTest {
    private val definition = ShieldSkillDefinition(
        id = 1L,
        unlockCost = 5,
        levels = listOf(
            ActiveSkillLevelParameters(
                durationMs = 2_000,
                cooldownMs = 20_000
            )
        )
    )

    @Test
    fun `unlocks skill at first level and spends skill points`() = runBlocking {
        val initialState = createPlayerState(skillPointAmount = 7)
        val playerRepository = FakePlayerRepository(initialState)
        val useCase = createUseCase(
            playerRepository = playerRepository,
            definitions = listOf(definition)
        )

        useCase(definition.id)

        assertEquals(
            2,
            playerRepository.state?.playerProfile?.skillPointAmount
        )
        assertEquals(
            listOf(SkillProgress(skillId = definition.id, level = 1)),
            playerRepository.state?.skillProgresses
        )
    }

    @Test
    fun `fails when skill is already unlocked`() = runBlocking {
        val initialState = createPlayerState(
            skillPointAmount = 10,
            skillProgresses = listOf(
                SkillProgress(
                    skillId = definition.id,
                    level = 1
                )
            )
        )
        val playerRepository = FakePlayerRepository(initialState)
        val useCase = createUseCase(
            playerRepository = playerRepository,
            definitions = listOf(definition)
        )

        val exception = runCatching {
            useCase(definition.id)
        }.exceptionOrNull()

        assertTrue(exception is IllegalArgumentException)
        assertEquals(initialState, playerRepository.state)
    }

    @Test
    fun `fails when player lacks skill points`() = runBlocking {
        val initialState = createPlayerState(skillPointAmount = 4)
        val playerRepository = FakePlayerRepository(initialState)
        val useCase = createUseCase(
            playerRepository = playerRepository,
            definitions = listOf(definition)
        )

        val exception = runCatching {
            useCase(definition.id)
        }.exceptionOrNull()

        assertTrue(exception is IllegalArgumentException)
        assertEquals(initialState, playerRepository.state)
    }

    @Test
    fun `fails for unknown skill`() = runBlocking {
        val initialState = createPlayerState(skillPointAmount = 10)
        val playerRepository = FakePlayerRepository(initialState)
        val useCase = createUseCase(playerRepository)

        val exception = runCatching {
            useCase(skillId = 999L)
        }.exceptionOrNull()

        assertTrue(exception is IllegalArgumentException)
        assertEquals(initialState, playerRepository.state)
    }

    private fun createUseCase(
        playerRepository: FakePlayerRepository,
        definitions: List<SkillDefinition> = emptyList()
    ): UnlockPlayerSkillUseCase {
        return UnlockPlayerSkillUseCase(
            playerRepository = playerRepository,
            getSkillDefinitionByIdUseCase = GetSkillDefinitionByIdUseCase(
                FakeSkillDefinitionRepository(definitions)
            )
        )
    }
}