package ru.landilf.hellofbullets.domain.usecase.skills

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.fixtures.PlayerTestFixtures.createPlayerState
import ru.landilf.hellofbullets.domain.model.battle.common.skill.CooldownReductionBattleSkill
import ru.landilf.hellofbullets.domain.model.battle.common.skill.DurationBonusBattleSkill
import ru.landilf.hellofbullets.domain.model.battle.common.skill.PassiveBattleSkill
import ru.landilf.hellofbullets.domain.model.battle.common.skill.ShieldBattleSkill
import ru.landilf.hellofbullets.domain.model.battle.common.skill.TimeSlowBattleSkill
import ru.landilf.hellofbullets.domain.model.skills.PassiveSkillStat
import ru.landilf.hellofbullets.domain.model.skills.SkillProgress
import ru.landilf.hellofbullets.domain.model.skills.definition.ActiveSkillLevelParameters
import ru.landilf.hellofbullets.domain.model.skills.definition.PassiveSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.ShieldSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.SkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.TimeSlowLevelParameters
import ru.landilf.hellofbullets.domain.model.skills.definition.TimeSlowSkillDefinition
import ru.landilf.hellofbullets.domain.usecase.FakeSkillDefinitionRepository

class BuildBattleSkillLoadoutUseCaseTest {
    private val timeSlowDefinition = TimeSlowSkillDefinition(
        id = TIME_SLOW_SKILL_ID,
        unlockCost = 5,
        levels = listOf(
            TimeSlowLevelParameters(
                activeParameters = ActiveSkillLevelParameters(
                    durationMs = 3_000,
                    cooldownMs = 30_000
                ),
                slowdownPercent = 50f
            ),
            TimeSlowLevelParameters(
                activeParameters = ActiveSkillLevelParameters(
                    durationMs = 3_500,
                    cooldownMs = 30_000
                ),
                slowdownPercent = 55f
            )
        )
    )

    private val shieldDefinition = ShieldSkillDefinition(
        id = SHIELD_SKILL_ID,
        unlockCost = 5,
        levels = listOf(
            ActiveSkillLevelParameters(
                durationMs = 2_000,
                cooldownMs = 20_000
            ),
            ActiveSkillLevelParameters(
                durationMs = 2_500,
                cooldownMs = 20_000
            )
        )
    )

    private val durationBonusDefinition = PassiveSkillDefinition(
        id = DURATION_BONUS_SKILL_ID,
        unlockCost = 5,
        stat = PassiveSkillStat.DURATION_BONUS,
        bonusPercentByLevel = listOf(20f, 40f)
    )

    private val cooldownReductionDefinition = PassiveSkillDefinition(
        id = COOLDOWN_REDUCTION_SKILL_ID,
        unlockCost = 5,
        stat = PassiveSkillStat.COOLDOWN_REDUCTION,
        bonusPercentByLevel = listOf(15f, 20f)
    )

    @Test
    fun `builds time slow and duration bonus using current levels`() {
        val playerState = createPlayerState(
            firstSkillId = TIME_SLOW_SKILL_ID,
            secondSkillId = DURATION_BONUS_SKILL_ID,
            skillProgresses = listOf(
                SkillProgress(TIME_SLOW_SKILL_ID, level = 2),
                SkillProgress(DURATION_BONUS_SKILL_ID, level = 2)
            )
        )
        val useCase = createUseCase()
        val result = useCase(playerState)

        assertEquals(
            TimeSlowBattleSkill(
                skillId = TIME_SLOW_SKILL_ID,
                durationMs = 3_500,
                cooldownMs = 30_000,
                slowdownPercent = 55f
            ),
            result.firstSlotSkill
        )
        assertEquals(
            DurationBonusBattleSkill(
                skillId = DURATION_BONUS_SKILL_ID,
                bonusPercent = 40f
            ),
            result.secondSlotSkill
        )
        assertEquals(
            listOf(result.secondSlotSkill),
            result.passiveSkills()
        )
    }

    @Test
    fun `builds shield and cooldown reduction using current levels`() {
        val playerState = createPlayerState(
            firstSkillId = SHIELD_SKILL_ID,
            secondSkillId = COOLDOWN_REDUCTION_SKILL_ID,
            skillProgresses = listOf(
                SkillProgress(SHIELD_SKILL_ID, level = 2),
                SkillProgress(COOLDOWN_REDUCTION_SKILL_ID, level = 2)
            )
        )
        val useCase = createUseCase()
        val result = useCase(playerState)

        assertEquals(
            ShieldBattleSkill(
                skillId = SHIELD_SKILL_ID,
                durationMs = 2_500,
                cooldownMs = 20_000
            ),
            result.firstSlotSkill
        )
        assertEquals(
            CooldownReductionBattleSkill(
                skillId = COOLDOWN_REDUCTION_SKILL_ID,
                bonusPercent = 20f
            ),
            result.secondSlotSkill
        )
    }

    @Test
    fun `builds empty loadout when player has no equipped skills`() {
        val useCase = createUseCase()
        val result = useCase(createPlayerState())

        assertEquals(null, result.firstSlotSkill)
        assertEquals(null, result.secondSlotSkill)
        assertEquals(emptyList<PassiveBattleSkill>(), result.passiveSkills())
    }

    private fun createUseCase(
        definitions: List<SkillDefinition> = listOf(
            timeSlowDefinition,
            shieldDefinition,
            durationBonusDefinition,
            cooldownReductionDefinition
        )
    ): BuildBattleSkillLoadoutUseCase {
        return BuildBattleSkillLoadoutUseCase(
            getSkillDefinitionByIdUseCase = GetSkillDefinitionByIdUseCase(
                skillDefinitionRepository = FakeSkillDefinitionRepository(definitions)
            )
        )
    }

    private companion object {
        const val TIME_SLOW_SKILL_ID = 1L
        const val SHIELD_SKILL_ID = 2L
        const val DURATION_BONUS_SKILL_ID = 3L
        const val COOLDOWN_REDUCTION_SKILL_ID = 4L
    }
}