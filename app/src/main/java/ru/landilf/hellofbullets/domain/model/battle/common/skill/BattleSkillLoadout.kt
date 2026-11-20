package ru.landilf.hellofbullets.domain.model.battle.common.skill

import ru.landilf.hellofbullets.domain.model.skills.SkillSlot

data class BattleSkillLoadout(
    val firstSlotSkill: BattleSkill?,
    val secondSlotSkill: BattleSkill?
) {
    init {
        require(
            firstSlotSkill?.skillId == null ||
                    firstSlotSkill.skillId != secondSlotSkill?.skillId
        ) {
            "Один навык нельзя использовать в обоих слотах"
        }
    }

    fun skillIn(
        slot: SkillSlot
    ): BattleSkill? {
        return when (slot) {
            SkillSlot.FIRST -> firstSlotSkill
            SkillSlot.SECOND -> secondSlotSkill
        }
    }

    fun activeSkillIn(
        slot: SkillSlot
    ): ActiveBattleSkill? {
        return skillIn(slot) as? ActiveBattleSkill
    }

    fun passiveSkills(): List<PassiveBattleSkill> {
        return listOfNotNull(
            firstSlotSkill,
            secondSlotSkill
        ).filterIsInstance<PassiveBattleSkill>()
    }
}
