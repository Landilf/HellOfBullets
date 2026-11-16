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

class UpgradePlayerSkillUseCaseTest {
    private val definition = ShieldSkillDefinition(
        id = 1L,
        unlockCost = 5,
        levels = List(3) {
            ActiveSkillLevelParameters(
                durationMs = 2_000,
                cooldownMs = 20_000
            )
        }
    )

    @Test
    fun `upgrades skill and spends target level cost`() = runBlocking {
        val initialState = createPlayerState(
            skillPointAmount = 5,
            skillProgresses = listOf(
                SkillProgress(skillId = definition.id, level = 2),
                SkillProgress(skillId = 2L, level = 1)
            )
        )
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
            listOf(
                SkillProgress(skillId = definition.id, level = 3),
                SkillProgress(skillId = 2L, level = 1)
            ),
            playerRepository.state?.skillProgresses
        )
    }

    @Test
    fun `fails when skill is not unlocked`() = runBlocking {
        val initialState = createPlayerState(skillPointAmount = 10)
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
    fun `fails when skill has maximum level`() = runBlocking {
        val initialState = createPlayerState(
            skillPointAmount = 10,
            skillProgresses = listOf(
                SkillProgress(
                    skillId = definition.id,
                    level = definition.maxLevel
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
    fun `fails when player locks target level cost`() = runBlocking {
        val initialState = createPlayerState(
            skillPointAmount = 2,
            skillProgresses = listOf(
                SkillProgress(
                    skillId = definition.id,
                    level = 2
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

    private fun createUseCase(
        playerRepository: FakePlayerRepository,
        definitions: List<SkillDefinition>
    ): UpgradePlayerSkillUseCase {
        return UpgradePlayerSkillUseCase(
            playerRepository = playerRepository,
            getSkillDefinitionByIdUseCase = GetSkillDefinitionByIdUseCase(
                FakeSkillDefinitionRepository(definitions)
            )
        )
    }
}