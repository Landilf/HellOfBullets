package ru.landilf.hellofbullets.domain.engine.battle.common

import ru.landilf.hellofbullets.domain.model.battle.common.projectile.BulletProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.Projectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.RocketProjectile
import ru.landilf.hellofbullets.domain.model.common.Vector2
import ru.landilf.hellofbullets.domain.model.player.PlayerRuntimeState
import javax.inject.Inject

class PlayerCollisionChecker @Inject constructor() {

    fun hasCollision(
        player: PlayerRuntimeState,
        projectiles: List<Projectile>
    ): Boolean {
        return projectiles.any { projectile ->
            hasCollisionWithProjectile(
                player = player,
                projectile = projectile
            )
        }
    }

    private fun hasCollisionWithProjectile(
        player: PlayerRuntimeState,
        projectile: Projectile
    ): Boolean {
        return when (projectile) {
            is BulletProjectile -> hasCircleCollision(
                playerPosition = player.position,
                playerRadius = player.hitRadius,
                projectilePosition = projectile.position,
                projectileRadius = projectile.hitRadius
            )

            is LaserProjectile -> hasLaserCollision(
                playerPosition = player.position,
                playerRadius = player.hitRadius,
                laserStart = projectile.startPosition,
                laserEnd = projectile.endPosition,
                laserRadius = projectile.hitRadius
            )

            is RocketProjectile -> hasCircleCollision(
                playerPosition = player.position,
                playerRadius = player.hitRadius,
                projectilePosition = projectile.position,
                projectileRadius = projectile.hitRadius
            )
        }
    }

    private fun hasCircleCollision(
        playerPosition: Vector2,
        playerRadius: Float,
        projectilePosition: Vector2,
        projectileRadius: Float
    ): Boolean {
        val dx = playerPosition.x - projectilePosition.x
        val dy = playerPosition.y - projectilePosition.y
        val combinedRadius = playerRadius + projectileRadius

        return dx * dx + dy * dy <= combinedRadius * combinedRadius
    }

    private fun hasLaserCollision(
        playerPosition: Vector2,
        playerRadius: Float,
        laserStart: Vector2,
        laserEnd: Vector2,
        laserRadius: Float
    ): Boolean {
        val distanceToSegment = calculateDistanceToSegment(
            point = playerPosition,
            segmentStart = laserStart,
            segmentEnd = laserEnd
        )

        return distanceToSegment <= playerRadius + laserRadius
    }

    private fun calculateDistanceToSegment(
        point: Vector2,
        segmentStart: Vector2,
        segmentEnd: Vector2
    ): Float {
        val segmentDx = segmentEnd.x - segmentStart.x
        val segmentDy = segmentEnd.y - segmentStart.y

        if (segmentDx == 0f && segmentDy == 0f) {
            return calculateDistance(point, segmentStart)
        }

        val projection = (
                (point.x - segmentStart.x) * segmentDx +
                        (point.y - segmentStart.y) * segmentDy
                ) / (segmentDx * segmentDx + segmentDy * segmentDy)

        val closestPointFactor = projection.coerceIn(0f, 1f)

        val closestPoint = Vector2(
            x = segmentStart.x + segmentDx * closestPointFactor,
            y = segmentStart.y + segmentDy * closestPointFactor
        )

        return calculateDistance(point, closestPoint)
    }

    private fun calculateDistance(
        first: Vector2,
        second: Vector2
    ): Float {
        val dx = first.x - second.x
        val dy = first.y - second.y

        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}