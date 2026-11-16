package ru.landilf.hellofbullets.data.catalog.skills

import ru.landilf.hellofbullets.domain.model.skills.PassiveSkillStat
import ru.landilf.hellofbullets.domain.model.skills.definition.ActiveSkillLevelParameters
import ru.landilf.hellofbullets.domain.model.skills.definition.PassiveSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.ShieldSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.SkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.TimeSlowLevelParameters
import ru.landilf.hellofbullets.domain.model.skills.definition.TimeSlowSkillDefinition
import ru.landilf.hellofbullets.domain.repository.SkillDefinitionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaticSkillDefinitionRepository @Inject constructor() : SkillDefinitionRepository {
    private val definitions = listOf(
        TimeSlowSkillDefinition(
            id = TIME_SLOW_SKILL_ID,
            unlockCost = UNLOCK_COST,
            levels = List(SKILL_LEVEL_COUNT) { levelIndex ->
                TimeSlowLevelParameters(
                    activeParameters = ActiveSkillLevelParameters(
                        durationMs = TIME_SLOW_BASE_DURATION_MS + levelIndex * DURATION_STEP_MS,
                        cooldownMs = TIME_SLOW_COOLDOWN_MS
                    ),
                    slowdownPercent = TIME_SLOW_BASE_PERCENT + levelIndex * TIME_SLOW_PERCENT_STEP
                )
            }
        ),
        ShieldSkillDefinition(
            id = SHIELD_SKILL_ID,
            unlockCost = UNLOCK_COST,
            levels = List(SKILL_LEVEL_COUNT) { levelIndex ->
                ActiveSkillLevelParameters(
                    durationMs = SHIELD_BASE_DURATION_MS + levelIndex * DURATION_STEP_MS,
                    cooldownMs = SHIELD_COOLDOWN_MS
                )
            }
        ),
        PassiveSkillDefinition(
            id = DURATION_BONUS_SKILL_ID,
            unlockCost = UNLOCK_COST,
            stat = PassiveSkillStat.DURATION_BONUS,
            bonusPercentByLevel = List(SKILL_LEVEL_COUNT) { levelIndex ->
                DURATION_BONUS_BASE_PERCENT + levelIndex * DURATION_BONUS_STEP_PERCENT
            }
        ),
        PassiveSkillDefinition(
            id = COOLDOWN_REDUCTION_SKILL_ID,
            unlockCost = UNLOCK_COST,
            stat = PassiveSkillStat.COOLDOWN_REDUCTION,
            bonusPercentByLevel = List(SKILL_LEVEL_COUNT) { levelIndex ->
                COOLDOWN_REDUCTION_BASE_PERCENT + levelIndex * COOLDOWN_REDUCTION_STEP_PERCENT
            }
        )
    )

    override fun getDefinitions(): List<SkillDefinition> {
        return definitions
    }

    override fun getDefinitionById(id: Long): SkillDefinition? {
        return definitions.firstOrNull { definition ->
            definition.id == id
        }
    }

    private companion object {
        const val TIME_SLOW_SKILL_ID = 1L
        const val SHIELD_SKILL_ID = 2L
        const val DURATION_BONUS_SKILL_ID = 3L
        const val COOLDOWN_REDUCTION_SKILL_ID = 4L

        const val UNLOCK_COST = 5
        const val SKILL_LEVEL_COUNT = 5

        const val TIME_SLOW_BASE_DURATION_MS = 3_000
        const val TIME_SLOW_COOLDOWN_MS = 30_000
        const val TIME_SLOW_BASE_PERCENT = 50f
        const val TIME_SLOW_PERCENT_STEP = 5f

        const val SHIELD_BASE_DURATION_MS = 2_000
        const val SHIELD_COOLDOWN_MS = 20_000
        const val DURATION_STEP_MS = 500

        const val DURATION_BONUS_BASE_PERCENT = 20f
        const val DURATION_BONUS_STEP_PERCENT = 20f
        const val COOLDOWN_REDUCTION_BASE_PERCENT = 15f
        const val COOLDOWN_REDUCTION_STEP_PERCENT = 5f
    }
}