package ru.landilf.hellofbullets.domain.model.battle.survival

import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackWave

data class SurvivalWaveState(
    val waves: List<AttackWave>,
    val currentWaveIndex: Int,
    val phase: SurvivalWavePhase,
    val timeUntilPhaseEndMs: Int,
    val currentPatternIndex: Int,
    val timeUntilNextVolleyMs: Int
) {
    val currentWave: AttackWave get() = waves[currentWaveIndex]
}
