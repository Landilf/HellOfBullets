package ru.landilf.hellofbullets.domain.engine.battle.common

import ru.landilf.hellofbullets.domain.model.battle.common.projectile.BulletProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserPhase
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.Projectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.RocketProjectile
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize
import ru.landilf.hellofbullets.domain.model.common.Vector2
import javax.inject.Inject

class ProjectileMovementUpdater @Inject constructor() {

    fun update(
        projectiles: List<Projectile>,
        deltaTimeMs: Int,
        fieldSize: GameFieldSize
    ): List<Projectile> {
        return projectiles.mapNotNull { projectile ->
            updateProjectile(
                projectile = projectile,
                deltaTimeMs = deltaTimeMs,
                fieldSize = fieldSize
            )
        }
    }

    private fun updateProjectile(
        projectile: Projectile,
        deltaTimeMs: Int,
        fieldSize: GameFieldSize
    ): Projectile? {
        val updatedLifetimeMs = projectile.remainingLifetimeMs - deltaTimeMs

        if (updatedLifetimeMs <= 0) {
            return null
        }

        return when (projectile) {
            is BulletProjectile -> updateBulletProjectile(
                projectile = projectile,
                deltaTimeMs = deltaTimeMs,
                updatedLifetimeMs = updatedLifetimeMs,
                fieldSize = fieldSize
            )

            is LaserProjectile -> updateLaserProjectile(
                projectile = projectile,
                deltaTimeMs = deltaTimeMs,
                updatedLifetimeMs = updatedLifetimeMs
            )

            is RocketProjectile -> updateRocketProjectile(
                projectile = projectile,
                deltaTimeMs = deltaTimeMs,
                updatedLifetimeMs = updatedLifetimeMs,
                fieldSize = fieldSize
            )
        }
    }

    private fun updateBulletProjectile(
        projectile: BulletProjectile,
        deltaTimeMs: Int,
        updatedLifetimeMs: Int,
        fieldSize: GameFieldSize
    ): BulletProjectile? {
        val updatedPosition = movePosition(
            position = projectile.position,
            velocity = projectile.velocity,
            deltaTimeMs = deltaTimeMs
        )

        if (!isInsideGameBounds(updatedPosition, fieldSize)) {
            return null
        }

        return projectile.copy(
            position = updatedPosition,
            remainingLifetimeMs = updatedLifetimeMs
        )
    }

    private fun updateLaserProjectile(
        projectile: LaserProjectile,
        deltaTimeMs: Int,
        updatedLifetimeMs: Int
    ): LaserProjectile {
        val updatedWarningMs = (projectile.remainingWarningMs - deltaTimeMs
                ).coerceAtLeast(0)

        return projectile.copy(
            remainingLifetimeMs = updatedLifetimeMs,
            phase = if (updatedWarningMs == 0) {
                LaserPhase.ACTIVE
            } else {
                LaserPhase.WARNING
            },
            remainingWarningMs = updatedWarningMs
        )
    }

    private fun updateRocketProjectile(
        projectile: RocketProjectile,
        deltaTimeMs: Int,
        updatedLifetimeMs: Int,
        fieldSize: GameFieldSize
    ): RocketProjectile? {
        val updatedPosition = movePosition(
            position = projectile.position,
            velocity = projectile.velocity,
            deltaTimeMs = deltaTimeMs
        )

        val updatedHomingTimeMs = (projectile.remainingHomingTimeMs - deltaTimeMs)
            .coerceAtLeast(0)

        if (!isInsideGameBounds(updatedPosition, fieldSize)) {
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
        position: Vector2,
        fieldSize: GameFieldSize
    ): Boolean {
        return position.x in -BOUNDARY_MARGIN..(fieldSize.width + BOUNDARY_MARGIN) &&
                position.y in -BOUNDARY_MARGIN..(fieldSize.height + BOUNDARY_MARGIN)
    }

    private companion object {
        const val BOUNDARY_MARGIN = 10f
    }
}