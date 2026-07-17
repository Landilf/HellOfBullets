package ru.landilf.hellofbullets.domain.model.config.survival

import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ProjectileType
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.SpawnZone

data class DefaultSurvivalWaveConfig(
    val waveId: Long,
    val patternId: Long,
    val projectileType: ProjectileType,
    val spawnZone: SpawnZone,
    val projectileCount: Int,
    val spawnIntervalMs: Int,
    val projectileSpeed: Float,
    val projectileDamage: Int,
    val projectileHitRadius: Float,
    val projectileLifetimeMs: Int,
    val waveDurationMs: Int,
    val breakAfterMs: Int
)
