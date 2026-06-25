package com.landilf.hellofbullets.data.skills

sealed class Skill {
    abstract val id: Long
    abstract val name: String
    abstract val description: String
    abstract val level: Int
    abstract val maxLevel: Int
}
