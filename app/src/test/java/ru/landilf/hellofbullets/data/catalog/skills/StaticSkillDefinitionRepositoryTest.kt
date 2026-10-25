package ru.landilf.hellofbullets.data.catalog.skills

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.skills.PassiveSkillStat
import ru.landilf.hellofbullets.domain.model.skills.definition.PassiveSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.ShieldSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.TimeSlowSkillDefinition
import ru.landilf.hellofbullets.fixtures.FLOAT_EPSILON

class StaticSkillDefinitionRepositoryTest {
    private val repository = StaticSkillDefinitionRepository()

    @Test
    fun `returns all configured skills with unique ids`() {
        val definitions = repository.getDefinitions()

        assertEquals(4, definitions.size)
        assertEquals(
            definitions.size,
            definitions.map { definition ->
                definition.id
            }.toSet().size
        )
    }

    @Test
    fun `returns skill with requested id`() {
        val definition = repository.getDefinitionById(1L)

        assertTrue(definition is TimeSlowSkillDefinition)
        assertEquals(1L, definition?.id)
    }

    @Test
    fun `returns null for unknown skill id`() {
        assertNull(repository.getDefinitionById(999L))
    }

    @Test
    fun `provides correct time slow parameters`() {
        val definition = repository.getDefinitions()
            .filterIsInstance<TimeSlowSkillDefinition>()
            .single()

        assertEquals(5, definition.maxLevel)
        assertEquals(5, definition.unlockCost)

        val firstLevel = definition.parametersFor(1)
        assertEquals(3_000, firstLevel.activeParameters.durationMs)
        assertEquals(30_000, firstLevel.activeParameters.cooldownMs)
        assertEquals(50f, firstLevel.slowdownPercent, FLOAT_EPSILON)

        val fifthLevel = definition.parametersFor(5)
        assertEquals(5_000, fifthLevel.activeParameters.durationMs)
        assertEquals(30_000, fifthLevel.activeParameters.cooldownMs)
        assertEquals(70f, fifthLevel.slowdownPercent, FLOAT_EPSILON)
    }

    @Test
    fun `provides correct shield parameters`() {
        val definition = repository.getDefinitions()
            .filterIsInstance<ShieldSkillDefinition>()
            .single()

        assertEquals(5, definition.maxLevel)
        assertEquals(5, definition.unlockCost)
        assertEquals(
            2_000,
            definition.parametersFor(1).durationMs
        )
        assertEquals(
            4_000,
            definition.parametersFor(5).durationMs
        )
        assertEquals(
            20_000,
            definition.parametersFor(1).cooldownMs
        )
        assertEquals(
            20_000,
            definition.parametersFor(5).cooldownMs
        )
    }

    @Test
    fun `provides correct passive skill bonuses`() {
        val definitions = repository.getDefinitions()
            .filterIsInstance<PassiveSkillDefinition>()

        val durationBonus = definitions.single { definition ->
            definition.stat == PassiveSkillStat.DURATION_BONUS
        }
        val cooldownReduction = definitions.single { definition ->
            definition.stat == PassiveSkillStat.COOLDOWN_REDUCTION
        }

        assertEquals(
            20f,
            durationBonus.bonusPercentFor(1),
            FLOAT_EPSILON
        )
        assertEquals(
            100f,
            durationBonus.bonusPercentFor(5),
            FLOAT_EPSILON
        )
        assertEquals(
            15f,
            cooldownReduction.bonusPercentFor(1),
            FLOAT_EPSILON
        )
        assertEquals(
            35f,
            cooldownReduction.bonusPercentFor(5),
            FLOAT_EPSILON
        )
    }
}