package ru.landilf.hellofbullets.domain.model.battle.common.projectile

data class ProjectileCreationResult(
    val projectiles: List<Projectile>,
    val nextProjectileId: Long
)
