package ru.landilf.hellofbullets.data.skills

data class ActiveSkill(
    override val id: Long,
    override val name: String,
    override val description: String,
    override val level: Int,
    override val maxLevel: Int,
    val cooldown: Int,
    val duration: Int
) : Skill()
