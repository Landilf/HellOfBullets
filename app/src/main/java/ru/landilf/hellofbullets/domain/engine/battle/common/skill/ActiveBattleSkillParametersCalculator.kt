package ru.landilf.hellofbullets.domain.engine.battle.common.skill

import ru.landilf.hellofbullets.domain.model.battle.common.skill.ActiveBattleSkill
import ru.landilf.hellofbullets.domain.model.battle.common.skill.BattleSkillLoadout
import ru.landilf.hellofbullets.domain.model.battle.common.skill.CooldownReductionBattleSkill
import ru.landilf.hellofbullets.domain.model.battle.common.skill.DurationBonusBattleSkill
import ru.landilf.hellofbullets.domain.model.player.PlayerStats
import javax.inject.Inject
import kotlin.math.roundToInt

class ActiveBattleSkillParametersCalculator @Inject constructor() {
    fun calculate(
        skill: ActiveBattleSkill,
        playerStats: PlayerStats,
        loadout: BattleSkillLoadout
    ): ActiveBattleSkillParameters {
        val durationBonusPercent = playerStats.effectDurationBonus + loadout.passiveSkills()
            .filterIsInstance<DurationBonusBattleSkill>()
            .sumOf { passiveSkill ->
                passiveSkill.bonusPercent.toDouble()
            }.toFloat()

        val cooldownReductionPercent = (
                playerStats.cooldownReduction.toFloat() + loadout.passiveSkills()
                    .filterIsInstance<CooldownReductionBattleSkill>()
                    .sumOf { passiveSkill ->
                        passiveSkill.bonusPercent.toDouble()
                    }.toFloat()
                ).coerceIn(0f, PERCENT_BASE)

        val durationMs = (
                skill.durationMs * (1f + durationBonusPercent / PERCENT_BASE)
                ).roundToInt()

        val cooldownMs = (
                skill.cooldownMs * (
                        1f - cooldownReductionPercent / PERCENT_BASE
                        )
                ).roundToInt()
            .coerceAtLeast(MINIMUM_COOLDOWN_MS)

        return ActiveBattleSkillParameters(
            durationMs = durationMs,
            cooldownMs = cooldownMs
        )
    }

    private companion object {
        const val PERCENT_BASE = 100f
        const val MINIMUM_COOLDOWN_MS = 500
    }
}