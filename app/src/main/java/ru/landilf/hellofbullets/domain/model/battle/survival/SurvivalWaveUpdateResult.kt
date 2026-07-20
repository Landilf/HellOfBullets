package ru.landilf.hellofbullets.domain.model.battle.survival

import ru.landilf.hellofbullets.domain.model.battle.common.projectile.Projectile

data class SurvivalWaveUpdateResult(
    val waveState: SurvivalWaveState?,
    val spawnedProjectiles: List<Projectile>,
    val nextProjectileId: Long
)
