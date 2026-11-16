package ru.landilf.hellofbullets.presentation.skills

import ru.landilf.hellofbullets.domain.model.skills.SkillSlot

sealed interface SkillAction {
    data class OnUnlockClick(
        val skillId: Long
    ) : SkillAction

    data class OnUpgradeClick(
        val skillId: Long
    ) : SkillAction

    data class OnEquipClick(
        val skillId: Long,
        val slot: SkillSlot
    ) : SkillAction

    data class OnUnequipClick(
        val slot: SkillSlot
    ) : SkillAction
}