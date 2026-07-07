package ru.landilf.hellofbullets.domain.model.battle.common.attackpattern

data class AttackPattern(
    val id: Long,
    val projectileType: ProjectileType,
    val spawnZone: SpawnZone,
    val projectileCount: Int,
    val spawnIntervalMs: Int,
    val projectileSpeed: Float,
    val projectileDamage: Int,
    val projectileHitRadius: Float,
    val projectileLifetimeMs: Int
)
