package ru.landilf.hellofbullets.domain.engine.battle.common.skill

import ru.landilf.hellofbullets.domain.model.battle.common.skill.ActiveBattleSkillRuntimeState
import ru.landilf.hellofbullets.domain.model.battle.common.skill.BattleSkillRuntimeState
import javax.inject.Inject

class BattleSkillRuntimeUpdater @Inject constructor() {
    fun update(
        runtimeState: BattleSkillRuntimeState,
        deltaTimeMs: Int
    ): BattleSkillRuntimeState {
        require(deltaTimeMs >= 0) {
            "Длительность обновления не может быть отрицательной"
        }

        return runtimeState.copy(
            firstSlotActiveSkill = updateSkillState(
                state = runtimeState.firstSlotActiveSkill,
                deltaTimeMs = deltaTimeMs
            ),
            secondSlotActiveSkill = updateSkillState(
                state = runtimeState.secondSlotActiveSkill,
                deltaTimeMs = deltaTimeMs
            )
        )
    }

    private fun updateSkillState(
        state: ActiveBattleSkillRuntimeState?,
        deltaTimeMs: Int
    ): ActiveBattleSkillRuntimeState? {
        return state?.copy(
            remainingEffectMs = (state.remainingEffectMs - deltaTimeMs)
                .coerceAtLeast(0),
            remainingCooldownMs = (state.remainingCooldownMs - deltaTimeMs)
                .coerceAtLeast(0)
        )
    }
}