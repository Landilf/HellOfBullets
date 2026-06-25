package ru.landilf.hellofbullets.data.equipment

data class ArmorItem(
    override val id: Long,
    override val name: String,
    override val level: Int,
    override val maxLevel: Int,
    val hp: Int,
    val defense: Int
) : Item()