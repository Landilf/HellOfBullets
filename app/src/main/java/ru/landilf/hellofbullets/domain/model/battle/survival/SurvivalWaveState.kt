package ru.landilf.hellofbullets.domain.model.battle.survival

import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackWave

data class SurvivalWaveState(
    val currentWave: AttackWave,
    val currentPatternIndex: Int,
    val elapsedWaveTimeMs: Int,
    val timeUntilNextVolleyMs: Int
)
