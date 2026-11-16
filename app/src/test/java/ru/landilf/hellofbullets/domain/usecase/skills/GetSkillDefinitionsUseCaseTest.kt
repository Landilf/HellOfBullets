package ru.landilf.hellofbullets.domain.usecase.skills

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.skills.definition.ActiveSkillLevelParameters
import ru.landilf.hellofbullets.domain.model.skills.definition.ShieldSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.TimeSlowLevelParameters
import ru.landilf.hellofbullets.domain.model.skills.definition.TimeSlowSkillDefinition
import ru.landilf.hellofbullets.domain.usecase.FakeSkillDefinitionRepository

class GetSkillDefinitionsUseCaseTest {
    @Test
    fun `returns definitions in repository order`() {
        val definitions = listOf(
            ShieldSkillDefinition(
                id = 1L,
                unlockCost = 5,
                levels = listOf(
                    ActiveSkillLevelParameters(
                        durationMs = 2_000,
                        cooldownMs = 20_000
                    )
                )
            ),
            TimeSlowSkillDefinition(
                id = 2L,
                unlockCost = 5,
                levels = listOf(
                    TimeSlowLevelParameters(
                        activeParameters = ActiveSkillLevelParameters(
                            durationMs = 3_000,
                            cooldownMs = 30_000
                        ),
                        slowdownPercent = 50f
                    )
                )
            )
        )

        val useCase = GetSkillDefinitionsUseCase(
            FakeSkillDefinitionRepository(definitions)
        )

        val result = useCase()

        assertEquals(definitions, result)
    }
}