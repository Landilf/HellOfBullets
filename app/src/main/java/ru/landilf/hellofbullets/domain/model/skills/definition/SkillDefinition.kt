package ru.landilf.hellofbullets.domain.model.skills.definition

sealed class SkillDefinition {
    abstract val id: Long
    abstract val maxLevel: Int
    abstract val unlockCost: Int
}