package ru.landilf.hellofbullets.domain.engine.battle.common

import ru.landilf.hellofbullets.domain.model.battle.common.projectile.BulletProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.Projectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.RocketProjectile
import ru.landilf.hellofbullets.domain.model.common.Vector2

class ProjectileMovementUpdater {

    fun update(
        projectiles: List<Projectile>,
        deltaTimeMs: Int
    ): List<Projectile> {
        return projectiles.mapNotNull { projectile ->
            updateProjectile(
                projectile = projectile,
                deltaTimeMs = deltaTimeMs
            )
        }
    }

    private fun updateProjectile(
        projectile: Projectile,
        deltaTimeMs: Int
    ): Projectile? {
        val updatedLifetimeMs = projectile.remainingLifetimeMs - deltaTimeMs

        if (updatedLifetimeMs <= 0) {
            return null
        }

        return when (projectile) {
            is BulletProjectile -> updateBulletProjectile(
                projectile = projectile,
                deltaTimeMs = deltaTimeMs,
                updatedLifetimeMs = updatedLifetimeMs
            )

            is LaserProjectile -> updateLaserProjectile(
                projectile = projectile,
                updatedLifetimeMs = updatedLifetimeMs
            )

            is RocketProjectile -> updateRocketProjectile(
                projectile = projectile,
                deltaTimeMs = deltaTimeMs,
                updatedLifetimeMs = updatedLifetimeMs
            )
        }
    }

    private fun updateBulletProjectile(
        projectile: BulletProjectile,
        deltaTimeMs: Int,
        updatedLifetimeMs: Int
    ): BulletProjectile? {
        val updatedPosition = movePosition(
            position = projectile.position,
            velocity = projectile.velocity,
            deltaTimeMs = deltaTimeMs
        )

        if (!isInsideGameBounds(updatedPosition)) {
            return null
        }

        return projectile.copy(
            position = updatedPosition,
            remainingLifetimeMs = updatedLifetimeMs
        )
    }

    private fun updateLaserProjectile(
        projectile: LaserProjectile,
        updatedLifetimeMs: Int
    ): LaserProjectile {
        return projectile.copy(
            remainingLifetimeMs = updatedLifetimeMs
        )
    }

    private fun updateRocketProjectile(
        projectile: RocketProjectile,
        deltaTimeMs: Int,
        updatedLifetimeMs: Int
    ): RocketProjectile? {
        val updatedPosition = movePosition(
            position = projectile.position,
            velocity = projectile.velocity,
            deltaTimeMs = deltaTimeMs
        )

        val updatedHomingTimeMs = (projectile.remainingHomingTimeMs - deltaTimeMs)
            .coerceAtLeast(0)

        if (!isInsideGameBounds(updatedPosition)) {
            return null
        }

        return projectile.copy(
            position = updatedPosition,
            remainingLifetimeMs = updatedLifetimeMs,
            remainingHomingTimeMs = updatedHomingTimeMs
        )
    }

    private fun movePosition(
        position: Vector2,
        velocity: Vector2,
        deltaTimeMs: Int
    ): Vector2 {
        val deltaTimeSeconds = deltaTimeMs / 1000f

        return Vector2(
            x = position.x + velocity.x * deltaTimeSeconds,
            y = position.y + velocity.y * deltaTimeSeconds
        )
    }

    private fun isInsideGameBounds(
        position: Vector2
    ): Boolean {
        return position.x in -BOUNDARY_MARGIN..(1f + BOUNDARY_MARGIN) &&
                position.y in -BOUNDARY_MARGIN..(1f + BOUNDARY_MARGIN)
    }

    private companion object {
        const val BOUNDARY_MARGIN = 0.1f
    }
}