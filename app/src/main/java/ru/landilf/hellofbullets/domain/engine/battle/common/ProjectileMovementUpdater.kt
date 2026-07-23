package ru.landilf.hellofbullets.domain.engine.battle.common

import ru.landilf.hellofbullets.domain.model.battle.common.projectile.BulletProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserPhase
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.Projectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.RocketProjectile
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize
import ru.landilf.hellofbullets.domain.model.common.Vector2
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ProjectileMovementUpdater @Inject constructor() {

    fun update(
        projectiles: List<Projectile>,
        deltaTimeMs: Int,
        fieldSize: GameFieldSize,
        playerPosition: Vector2
    ): List<Projectile> {
        return projectiles.mapNotNull { projectile ->
            updateProjectile(
                projectile = projectile,
                deltaTimeMs = deltaTimeMs,
                fieldSize = fieldSize,
                playerPosition = playerPosition
            )
        }
    }

    private fun updateProjectile(
        projectile: Projectile,
        deltaTimeMs: Int,
        fieldSize: GameFieldSize,
        playerPosition: Vector2
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
                fieldSize = fieldSize,
                playerPosition = playerPosition
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
        fieldSize: GameFieldSize,
        playerPosition: Vector2
    ): RocketProjectile? {
        val homingDeltaTimeMs = minOf(
            deltaTimeMs,
            projectile.remainingHomingTimeMs
        )

        val updatedVelocity = if (homingDeltaTimeMs > 0) {
            rotateVelocityTowardsTarget(
                velocity = projectile.velocity,
                position = projectile.position,
                targetPosition = playerPosition,
                maxTurnRateRadiansPerSecond = projectile.maxTurnRateRadiansPerSecond,
                deltaTimeMs = homingDeltaTimeMs
            )
        } else {
            projectile.velocity
        }

        val positionAfterHoming = movePosition(
            position = projectile.position,
            velocity = updatedVelocity,
            deltaTimeMs = homingDeltaTimeMs
        )

        val updatedPosition = movePosition(
            position = positionAfterHoming,
            velocity = updatedVelocity,
            deltaTimeMs = deltaTimeMs - homingDeltaTimeMs
        )

        if (!isInsideGameBounds(updatedPosition, fieldSize)) {
            return null
        }

        return projectile.copy(
            position = updatedPosition,
            velocity = updatedVelocity,
            remainingLifetimeMs = updatedLifetimeMs,
            remainingHomingTimeMs = projectile.remainingHomingTimeMs - homingDeltaTimeMs
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

    private fun rotateVelocityTowardsTarget(
        velocity: Vector2,
        position: Vector2,
        targetPosition: Vector2,
        maxTurnRateRadiansPerSecond: Float,
        deltaTimeMs: Int
    ): Vector2 {
        val speed = sqrt(velocity.x * velocity.x + velocity.y * velocity.y)

        if (speed == 0f) {
            return velocity
        }

        val targetDeltaX = targetPosition.x - position.x
        val targetDeltaY = targetPosition.y - position.y

        if (targetDeltaX == 0f && targetDeltaY == 0f) {
            return velocity
        }

        val currentAngle = atan2(velocity.y, velocity.x)
        val targetAngle = atan2(targetDeltaY, targetDeltaX)
        val angleDifference = normalizedAngle(targetAngle - currentAngle)

        val maxTurnAngle = maxTurnRateRadiansPerSecond * deltaTimeMs / 1_000f

        val updatedAngle = currentAngle + angleDifference.coerceIn(
            -maxTurnAngle,
            maxTurnAngle
        )

        return Vector2(
            x = cos(updatedAngle) * speed,
            y = sin(updatedAngle) * speed
        )
    }

    private fun normalizedAngle(
        angle: Float
    ): Float {
        var normalizedAngle = angle
        val pi = Math.PI.toFloat()
        val twoPi = (Math.PI * 2).toFloat()

        while (normalizedAngle > pi) {
            normalizedAngle -= twoPi
        }

        while (normalizedAngle < -pi) {
            normalizedAngle += twoPi
        }

        return normalizedAngle
    }

    private companion object {
        const val BOUNDARY_MARGIN = 10f
    }
}