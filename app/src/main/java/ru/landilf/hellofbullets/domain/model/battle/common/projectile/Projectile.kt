package ru.landilf.hellofbullets.domain.model.battle.common.projectile

sealed class Projectile {
    abstract val id: Long
    abstract val damage: Int
    abstract val hitRadius: Float
    abstract val remainingLifetimeMs: Int
}