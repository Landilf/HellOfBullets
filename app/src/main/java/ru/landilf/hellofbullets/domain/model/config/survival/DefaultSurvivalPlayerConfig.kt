package ru.landilf.hellofbullets.domain.model.config.survival

data class DefaultSurvivalPlayerConfig(
    val maxHp: Int,
    val damage: Int,
    val defense: Int,
    val cooldownReduction: Int,
    val effectDurationBonus: Int,
    val hitRadius: Float
)
