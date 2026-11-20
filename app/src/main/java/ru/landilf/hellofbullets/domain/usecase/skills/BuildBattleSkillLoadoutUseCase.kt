package ru.landilf.hellofbullets.domain.usecase.skills

import ru.landilf.hellofbullets.domain.model.battle.common.skill.BattleSkill
import ru.landilf.hellofbullets.domain.model.battle.common.skill.BattleSkillLoadout
import ru.landilf.hellofbullets.domain.model.battle.common.skill.CooldownReductionBattleSkill
import ru.landilf.hellofbullets.domain.model.battle.common.skill.DurationBonusBattleSkill
import ru.landilf.hellofbullets.domain.model.battle.common.skill.ShieldBattleSkill
import ru.landilf.hellofbullets.domain.model.battle.common.skill.TimeSlowBattleSkill
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.model.skills.PassiveSkillStat
import ru.landilf.hellofbullets.domain.model.skills.definition.PassiveSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.ShieldSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.SkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.TimeSlowSkillDefinition
import javax.inject.Inject

class BuildBattleSkillLoadoutUseCase @Inject constructor(
    private val getSkillDefinitionByIdUseCase: GetSkillDefinitionByIdUseCase
) {
    operator fun invoke(
        playerState: PlayerState
    ): BattleSkillLoadout {
        return BattleSkillLoadout(
            firstSlotSkill = buildSkill(
                playerState = playerState,
                skillId = playerState.playerBuild.firstSkillId
            ),
            secondSlotSkill = buildSkill(
                playerState = playerState,
                skillId = playerState.playerBuild.secondSkillId
            )
        )
    }

    private fun buildSkill(
        playerState: PlayerState,
        skillId: Long?
    ): BattleSkill? {
        if (skillId == null) {
            return null
        }

        val progress = requireNotNull(
            playerState.skillProgresses.firstOrNull {
                it.skillId == skillId
            }
        ) {
            "Игрок не разблокировал навык с id $skillId"
        }
        val definition = requireNotNull(
            getSkillDefinitionByIdUseCase(skillId)
        ) {
            "Не найдено определение навыка с id $skillId"
        }

        return definition.toBattleSkill(
            level = progress.level
        )
    }

    private fun SkillDefinition.toBattleSkill(
        level: Int
    ): BattleSkill {
        return when (this) {
            is TimeSlowSkillDefinition -> {
                val parameters = parametersFor(level)

                TimeSlowBattleSkill(
                    skillId = id,
                    durationMs = parameters.activeParameters.durationMs,
                    cooldownMs = parameters.activeParameters.cooldownMs,
                    slowdownPercent = parameters.slowdownPercent
                )
            }

            is ShieldSkillDefinition -> {
                val parameters = parametersFor(level)

                ShieldBattleSkill(
                    skillId = id,
                    durationMs = parameters.durationMs,
                    cooldownMs = parameters.cooldownMs
                )
            }

            is PassiveSkillDefinition -> when (stat) {
                PassiveSkillStat.DURATION_BONUS -> {
                    DurationBonusBattleSkill(
                        skillId = id,
                        bonusPercent = bonusPercentFor(level)
                    )
                }

                PassiveSkillStat.COOLDOWN_REDUCTION -> {
                    CooldownReductionBattleSkill(
                        skillId = id,
                        bonusPercent = bonusPercentFor(level)
                    )
                }
            }
        }
    }
}