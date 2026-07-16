package ru.landilf.hellofbullets.domain.engine.battle.common

import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackPattern
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ProjectileType
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.SpawnZone
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.BulletProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.Projectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.RocketProjectile
import ru.landilf.hellofbullets.domain.model.common.Vector2
import javax.inject.Inject

class ProjectileFactory @Inject constructor() {
    private var nextProjectileId = 0L

    fun createVolley(
        pattern: AttackPattern
    ): List<Projectile> {
        return List(pattern.projectileCount) { index ->
            createProjectile(
                pattern = pattern,
                index = index
            )
        }
    }

    private fun createProjectile(
        pattern: AttackPattern,
        index: Int
    ): Projectile {
        return when (pattern.projectileType) {
            ProjectileType.BULLET -> createBulletProjectile(pattern, index)
            ProjectileType.LASER -> createLaserProjectile(pattern, index)
            ProjectileType.ROCKET -> createRocketProjectile(pattern, index)
        }
    }

    private fun createBulletProjectile(
        pattern: AttackPattern,
        index: Int
    ): BulletProjectile {
        return BulletProjectile(
            id = generateProjectileId(),
            damage = pattern.projectileDamage,
            hitRadius = pattern.projectileHitRadius,
            remainingLifetimeMs = pattern.projectileLifetimeMs,
            position = createSpawnPosition(pattern.spawnZone, index, pattern.projectileCount),
            velocity = createVelocity(pattern.spawnZone, pattern.projectileSpeed)
        )
    }

    private fun createLaserProjectile(
        pattern: AttackPattern,
        index: Int
    ): LaserProjectile {
        val startPosition = createSpawnPosition(
            spawnZone = pattern.spawnZone,
            index = index,
            totalCount = pattern.projectileCount
        )

        val endPosition = createLaserEndPosition(
            spawnZone = pattern.spawnZone,
            startPosition
        )

        return LaserProjectile(
            id = generateProjectileId(),
            damage = pattern.projectileDamage,
            hitRadius = pattern.projectileHitRadius,
            remainingLifetimeMs = pattern.projectileLifetimeMs,
            startPosition = startPosition,
            endPosition = endPosition
        )
    }

    private fun createRocketProjectile(
        pattern: AttackPattern,
        index: Int
    ): RocketProjectile {
        return RocketProjectile(
            id = generateProjectileId(),
            damage = pattern.projectileDamage,
            hitRadius = pattern.projectileHitRadius,
            remainingLifetimeMs = pattern.projectileLifetimeMs,
            position = createSpawnPosition(pattern.spawnZone, index, pattern.projectileCount),
            velocity = createVelocity(pattern.spawnZone, pattern.projectileSpeed),
            remainingHomingTimeMs = 1000
        )
    }

    private fun generateProjectileId(): Long {
        return nextProjectileId++
    }

    private fun createSpawnPosition(
        spawnZone: SpawnZone,
        index: Int,
        totalCount: Int
    ): Vector2 {
        val fraction = calculateFraction(index, totalCount)

        return when (spawnZone) {
            SpawnZone.LEFT_TOP -> Vector2(
                x = 0f,
                y = fraction * 0.5f
            )

            SpawnZone.LEFT_BOTTOM -> Vector2(
                x = 0f,
                y = 0.5f + fraction * 0.5f
            )

            SpawnZone.TOP -> Vector2(
                x = fraction,
                y = 0f
            )

            SpawnZone.RIGHT_TOP -> Vector2(
                x = 1f,
                y = fraction * 0.5f
            )

            SpawnZone.RIGHT_BOTTOM -> Vector2(
                x = 1f,
                y = 0.5f + fraction * 0.5f
            )

            SpawnZone.BOTTOM -> Vector2(
                x = fraction,
                y = 1f
            )
        }
    }

    private fun createVelocity(
        spawnZone: SpawnZone,
        speed: Float
    ): Vector2 {
        val direction = when (spawnZone) {
            SpawnZone.LEFT_TOP,
            SpawnZone.LEFT_BOTTOM -> Vector2(1f, 0f)

            SpawnZone.TOP -> Vector2(0f, 1f)

            SpawnZone.RIGHT_TOP,
            SpawnZone.RIGHT_BOTTOM -> Vector2(-1f, 0f)

            SpawnZone.BOTTOM -> Vector2(0f, -1f)
        }

        return Vector2(
            x = direction.x * speed,
            y = direction.y * speed
        )
    }

    private fun createLaserEndPosition(
        spawnZone: SpawnZone,
        startPosition: Vector2
    ): Vector2 {
        return when (spawnZone) {
            SpawnZone.LEFT_TOP,
            SpawnZone.LEFT_BOTTOM -> Vector2(
                x = 1f,
                y = startPosition.y
            )

            SpawnZone.TOP -> Vector2(
                x = startPosition.x,
                y = 1f
            )

            SpawnZone.RIGHT_TOP,
            SpawnZone.RIGHT_BOTTOM -> Vector2(
                x = 0f,
                y = startPosition.y
            )

            SpawnZone.BOTTOM -> Vector2(
                x = startPosition.x,
                y = 0f
            )
        }
    }

    private fun calculateFraction(
        index: Int,
        totalCount: Int
    ): Float {
        if (totalCount <= 1) {
            return 0.5f
        }

        return index.toFloat() / (totalCount - 1).toFloat()
    }
}