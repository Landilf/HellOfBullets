package ru.landilf.hellofbullets.domain.model.battle.common.skill

import ru.landilf.hellofbullets.domain.model.skills.SkillSlot

data class BattleSkillRuntimeState(
    val firstSlotActiveSkill: ActiveBattleSkillRuntimeState?,
    val secondSlotActiveSkill: ActiveBattleSkillRuntimeState?
) {
    fun activeSkillIn(
        slot: SkillSlot
    ): ActiveBattleSkillRuntimeState? {
        return when (slot) {
            SkillSlot.FIRST -> firstSlotActiveSkill
            SkillSlot.SECOND -> secondSlotActiveSkill
        }
    }

    fun withActiveSkillState(
        slot: SkillSlot,
        activeSkillState: ActiveBattleSkillRuntimeState?
    ): BattleSkillRuntimeState {
        return when (slot) {
            SkillSlot.FIRST -> copy(
                firstSlotActiveSkill = activeSkillState
            )

            SkillSlot.SECOND -> copy(
                secondSlotActiveSkill = activeSkillState
            )
        }
    }
}