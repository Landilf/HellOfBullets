package ru.landilf.hellofbullets.domain.model.battle.common.projectile

import ru.landilf.hellofbullets.domain.model.common.Vector2

data class RocketProjectile(
    override val id: Long,
    override val damage: Int,
    override val hitRadius: Float,
    override val remainingLifetimeMs: Int,
    val position: Vector2,
    val velocity: Vector2,
    val remainingHomingTimeMs: Int
) : Projectile()
