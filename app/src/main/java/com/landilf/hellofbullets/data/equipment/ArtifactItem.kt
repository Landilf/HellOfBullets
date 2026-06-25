package com.landilf.hellofbullets.data.equipment

data class ArtifactItem(
    override val id: Long,
    override val name: String,
    override val level: Int,
    override val maxLevel: Int,
    val cooldownReduction: Int,
    val duration: Int
) : Item()
