package ru.landilf.hellofbullets.domain.model.equipment

data class WeaponItem(
    override val id: Long,
    override val name: String,
    override val level: Int,
    override val maxLevel: Int,
    val damage: Int,
    val attackSpeed: Int
) : Item()
