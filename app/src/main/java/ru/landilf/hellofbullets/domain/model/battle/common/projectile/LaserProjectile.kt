package ru.landilf.hellofbullets.domain.model.battle.common.projectile

import ru.landilf.hellofbullets.domain.model.common.Vector2

data class LaserProjectile(
    override val id: Long,
    override val damage: Int,
    override val hitRadius: Float,
    override val remainingLifetimeMs: Int,
    val startPosition: Vector2,
    val endPosition: Vector2
) : Projectile()
