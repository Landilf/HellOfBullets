package ru.landilf.hellofbullets.domain.model.battle.common.attackpattern

data class AttackWave(
    val id: Long,
    val patterns: List<AttackPattern>,
    val durationMs: Int,
    val breakDurationMs: Int
)