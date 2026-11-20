package ru.landilf.hellofbullets.domain.engine.battle.common.skill

import ru.landilf.hellofbullets.domain.model.battle.common.skill.BattleSkillLoadout
import ru.landilf.hellofbullets.domain.model.battle.common.skill.BattleSkillRuntimeState
import ru.landilf.hellofbullets.domain.model.player.PlayerStats
import ru.landilf.hellofbullets.domain.model.skills.SkillSlot
import javax.inject.Inject

class BattleSkillActivator @Inject constructor(
    private val parametersCalculator: ActiveBattleSkillParametersCalculator
) {
    fun activate(
        runtimeState: BattleSkillRuntimeState,
        loadout: BattleSkillLoadout,
        playerStats: PlayerStats,
        slot: SkillSlot
    ): BattleSkillRuntimeState {
        val activeSkillState = runtimeState.activeSkillIn(slot)
            ?: return runtimeState

        if (!activeSkillState.isAvailable) {
            return runtimeState
        }

        val parameters = parametersCalculator.calculate(
            skill = activeSkillState.skill,
            playerStats = playerStats,
            loadout = loadout
        )

        return runtimeState.withActiveSkillState(
            slot = slot,
            activeSkillState = activeSkillState.copy(
                remainingEffectMs = parameters.durationMs,
                remainingCooldownMs = parameters.cooldownMs
            )
        )
    }
}