package ru.landilf.hellofbullets.presentation.skills

import androidx.annotation.StringRes
import ru.landilf.hellofbullets.domain.model.skills.SkillSlot

data class SkillUiModel(
    val skillId: Long,
    @get:StringRes val nameRes: Int,
    @get:StringRes val descriptionRes: Int,
    val level: Int?,
    val maxLevel: Int,
    val unlockCost: Int,
    val nextUpgradeCost: Int?,
    val equippedSlot: SkillSlot?
) {
    val isUnlocked: Boolean
        get() = level != null

    val isMaxLevel: Boolean
        get() = level == maxLevel
}