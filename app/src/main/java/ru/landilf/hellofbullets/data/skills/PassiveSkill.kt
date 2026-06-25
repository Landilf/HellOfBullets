package ru.landilf.hellofbullets.data.skills

data class PassiveSkill(
    override val id: Long,
    override val name: String,
    override val description: String,
    override val level: Int,
    override val maxLevel: Int
) : Skill()
