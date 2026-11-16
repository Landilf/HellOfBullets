package ru.landilf.hellofbullets.presentation.skills

import androidx.annotation.StringRes
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.domain.model.skills.PassiveSkillStat
import ru.landilf.hellofbullets.domain.model.skills.SkillProgress
import ru.landilf.hellofbullets.domain.model.skills.SkillSlot
import ru.landilf.hellofbullets.domain.model.skills.definition.PassiveSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.ShieldSkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.SkillDefinition
import ru.landilf.hellofbullets.domain.model.skills.definition.TimeSlowSkillDefinition

fun SkillDefinition.toUiModel(
    progress: SkillProgress?,
    equippedSlot: SkillSlot?
): SkillUiModel {
    val level = progress?.level

    return SkillUiModel(
        skillId = id,
        nameRes = nameRes(),
        descriptionRes = descriptionRes(),
        level = level,
        maxLevel = maxLevel,
        unlockCost = unlockCost,
        nextUpgradeCost = level
            ?.takeIf { it < maxLevel }
            ?.plus(1),
        equippedSlot = equippedSlot
    )
}

@StringRes
private fun SkillDefinition.nameRes(): Int {
    return when (this) {
        is TimeSlowSkillDefinition -> R.string.skill_time_slow_name
        is ShieldSkillDefinition -> R.string.skill_shield_name
        is PassiveSkillDefinition -> when (stat) {
            PassiveSkillStat.DURATION_BONUS -> R.string.skill_duration_bonus_name
            PassiveSkillStat.COOLDOWN_REDUCTION -> R.string.skill_cooldown_reduction_name
        }
    }
}

@StringRes
private fun SkillDefinition.descriptionRes(): Int {
    return when (this) {
        is TimeSlowSkillDefinition -> R.string.skill_time_slow_description
        is ShieldSkillDefinition -> R.string.skill_shield_description
        is PassiveSkillDefinition -> when (stat) {
            PassiveSkillStat.DURATION_BONUS -> R.string.skill_duration_bonus_description
            PassiveSkillStat.COOLDOWN_REDUCTION -> R.string.skill_cooldown_reduction_description
        }
    }
}