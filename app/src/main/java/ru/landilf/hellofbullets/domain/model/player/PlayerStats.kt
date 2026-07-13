package ru.landilf.hellofbullets.domain.model.player

data class PlayerStats(
    val maxHp: Int,
    val damage: Int,
    val defense: Int,
    val cooldownReduction: Int,
    val effectDurationBonus: Int
)
