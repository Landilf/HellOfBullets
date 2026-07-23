package ru.landilf.hellofbullets.domain.engine.battle.common

import ru.landilf.hellofbullets.domain.engine.battle.common.random.BattleRandomGenerator
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ArenaEdgeSection
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackPattern
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ProjectileType
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.BulletProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserPhase
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.Projectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.ProjectileCreationResult
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.ProjectileGenerationState
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.RocketProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.random.BattleRandomState
import ru.landilf.hellofbullets.domain.model.battle.common.random.RandomResult
import ru.landilf.hellofbullets.domain.model.common.FloatRange
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize
import ru.landilf.hellofbullets.domain.model.common.Vector2
import javax.inject.Inject
import kotlin.math.sqrt

class ProjectileFactory @Inject constructor(
    private val randomGenerator: BattleRandomGenerator
) {
    fun createVolley(
        pattern: AttackPattern,
        generationState: ProjectileGenerationState,
        fieldSize: GameFieldSize
    ): ProjectileCreationResult {
        var currentGenerationState = generationState

        val projectiles = buildList {
            repeat(pattern.projectileCount) {
                val result = createProjectile(
                    pattern = pattern,
                    generationState = currentGenerationState,
                    fieldSize = fieldSize
                )

                add(result.projectile)

                currentGenerationState = currentGenerationState.copy(
                    nextProjectileId = currentGenerationState.nextProjectileId + 1,
                    randomState = result.nextRandomState
                )
            }
        }

        return ProjectileCreationResult(
            projectiles = projectiles,
            nextGenerationState = currentGenerationState
        )
    }

    private fun createProjectile(
        pattern: AttackPattern,
        generationState: ProjectileGenerationState,
        fieldSize: GameFieldSize
    ): RandomProjectileResult {
        return when (pattern.projectileType) {
            ProjectileType.BULLET -> createBulletProjectile(
                pattern = pattern,
                generationState = generationState,
                fieldSize = fieldSize
            )

            ProjectileType.LASER -> createLaserProjectile(
                pattern = pattern,
                generationState = generationState,
                fieldSize = fieldSize
            )

            ProjectileType.ROCKET -> createRocketProjectile(
                pattern = pattern,
                generationState = generationState,
                fieldSize = fieldSize
            )
        }
    }

    private fun createBulletProjectile(
        pattern: AttackPattern,
        generationState: ProjectileGenerationState,
        fieldSize: GameFieldSize
    ): RandomProjectileResult {
        val pathResult = createProjectilePath(
            pattern = pattern,
            randomState = generationState.randomState,
            fieldSize = fieldSize
        )
        val velocityResult = createVelocity(
            startPosition = pathResult.value.startPosition,
            targetPosition = pathResult.value.targetPosition,
            fallbackSection = pattern.spawnSection,
            speedRange = pattern.projectileSpeedRange,
            randomState = pathResult.nextState
        )

        return RandomProjectileResult(
            projectile = BulletProjectile(
                id = generationState.nextProjectileId,
                damage = pattern.projectileDamage,
                hitRadius = pattern.projectileHitRadius,
                remainingLifetimeMs = pattern.projectileLifetimeMs,
                position = pathResult.value.startPosition,
                velocity = velocityResult.value
            ),
            nextRandomState = velocityResult.nextState
        )
    }

    private fun createLaserProjectile(
        pattern: AttackPattern,
        generationState: ProjectileGenerationState,
        fieldSize: GameFieldSize
    ): RandomProjectileResult {
        val pathResult = createProjectilePath(
            pattern = pattern,
            randomState = generationState.randomState,
            fieldSize = fieldSize
        )
        val warningDurationMs = pattern.warningDurationMs

        return RandomProjectileResult(
            projectile = LaserProjectile(
                id = generationState.nextProjectileId,
                damage = pattern.projectileDamage,
                hitRadius = pattern.projectileHitRadius,
                remainingLifetimeMs = pattern.projectileLifetimeMs + warningDurationMs,
                startPosition = pathResult.value.startPosition,
                endPosition = pathResult.value.targetPosition,
                phase = if (warningDurationMs > 0) {
                    LaserPhase.WARNING
                } else {
                    LaserPhase.ACTIVE
                },
                remainingWarningMs = warningDurationMs
            ),
            nextRandomState = pathResult.nextState
        )
    }

    private fun createRocketProjectile(
        pattern: AttackPattern,
        generationState: ProjectileGenerationState,
        fieldSize: GameFieldSize
    ): RandomProjectileResult {
        val homingConfig = requireNotNull(pattern.rocketHomingConfig) {
            "AttackPattern должен содержать конфигурацию самонаведения для ракеты"
        }

        val pathResult = createProjectilePath(
            pattern = pattern,
            randomState = generationState.randomState,
            fieldSize = fieldSize
        )
        val velocityResult = createVelocity(
            startPosition = pathResult.value.startPosition,
            targetPosition = pathResult.value.targetPosition,
            fallbackSection = pattern.spawnSection,
            speedRange = pattern.projectileSpeedRange,
            randomState = pathResult.nextState
        )

        return RandomProjectileResult(
            projectile = RocketProjectile(
                id = generationState.nextProjectileId,
                damage = pattern.projectileDamage,
                hitRadius = pattern.projectileHitRadius,
                remainingLifetimeMs = pattern.projectileLifetimeMs,
                position = pathResult.value.startPosition,
                velocity = velocityResult.value,
                remainingHomingTimeMs = homingConfig.durationMs,
                maxTurnRateRadiansPerSecond = homingConfig.maxTurnRateRadiansPerSecond
            ),
            nextRandomState = velocityResult.nextState
        )
    }

    private fun createProjectilePath(
        pattern: AttackPattern,
        randomState: BattleRandomState,
        fieldSize: GameFieldSize
    ): RandomResult<ProjectilePath> {
        val spawnPositionResult = createRandomPosition(
            section = pattern.spawnSection,
            randomState = randomState,
            fieldSize = fieldSize
        )
        val targetSectionResult = randomGenerator.pick(
            randomState = spawnPositionResult.nextState,
            values = pattern.targetSections
        )
        val targetPositionResult = createRandomPosition(
            section = targetSectionResult.value,
            randomState = targetSectionResult.nextState,
            fieldSize = fieldSize
        )

        return RandomResult(
            value = ProjectilePath(
                startPosition = spawnPositionResult.value,
                targetPosition = targetPositionResult.value
            ),
            nextState = targetPositionResult.nextState
        )
    }

    private fun createRandomPosition(
        section: ArenaEdgeSection,
        randomState: BattleRandomState,
        fieldSize: GameFieldSize
    ): RandomResult<Vector2> {
        return when (section) {
            ArenaEdgeSection.TOP -> {
                val xResult = randomGenerator.nextFloat(
                    randomState = randomState,
                    range = FloatRange(0f, fieldSize.width)
                )

                RandomResult(
                    value = Vector2(
                        x = xResult.value,
                        y = 0f
                    ),
                    nextState = xResult.nextState
                )
            }

            ArenaEdgeSection.BOTTOM -> {
                val xResult = randomGenerator.nextFloat(
                    randomState = randomState,
                    range = FloatRange(0f, fieldSize.width)
                )

                RandomResult(
                    value = Vector2(
                        x = xResult.value,
                        y = fieldSize.height
                    ),
                    nextState = xResult.nextState
                )
            }

            ArenaEdgeSection.LEFT_UPPER -> {
                val yResult = randomGenerator.nextFloat(
                    randomState = randomState,
                    range = FloatRange(0f, fieldSize.height / 2f)
                )

                RandomResult(
                    value = Vector2(
                        x = 0f,
                        y = yResult.value
                    ),
                    nextState = yResult.nextState
                )
            }

            ArenaEdgeSection.LEFT_LOWER -> {
                val yResult = randomGenerator.nextFloat(
                    randomState = randomState,
                    range = FloatRange(fieldSize.height / 2f, fieldSize.height)
                )

                RandomResult(
                    value = Vector2(
                        x = 0f,
                        y = yResult.value
                    ),
                    nextState = yResult.nextState
                )
            }

            ArenaEdgeSection.RIGHT_UPPER -> {
                val yResult = randomGenerator.nextFloat(
                    randomState = randomState,
                    range = FloatRange(0f, fieldSize.height / 2f)
                )

                RandomResult(
                    value = Vector2(
                        x = fieldSize.width,
                        y = yResult.value
                    ),
                    nextState = yResult.nextState
                )
            }

            ArenaEdgeSection.RIGHT_LOWER -> {
                val yResult = randomGenerator.nextFloat(
                    randomState = randomState,
                    range = FloatRange(fieldSize.height / 2f, fieldSize.height)
                )

                RandomResult(
                    value = Vector2(
                        x = fieldSize.width,
                        y = yResult.value
                    ),
                    nextState = yResult.nextState
                )
            }
        }
    }

    private fun createVelocity(
        startPosition: Vector2,
        targetPosition: Vector2,
        fallbackSection: ArenaEdgeSection,
        speedRange: FloatRange,
        randomState: BattleRandomState
    ): RandomResult<Vector2> {
        val speedResult = randomGenerator.nextFloat(
            randomState = randomState,
            range = speedRange
        )

        val deltaX = targetPosition.x - startPosition.x
        val deltaY = targetPosition.y - startPosition.y
        val distance = sqrt(deltaX * deltaX + deltaY * deltaY)

        val direction = if (distance > 0f) {
            Vector2(
                x = deltaX / distance,
                y = deltaY / distance
            )
        } else {
            createFallbackDirection(fallbackSection)
        }

        return RandomResult(
            value = Vector2(
                x = direction.x * speedResult.value,
                y = direction.y * speedResult.value
            ),
            nextState = speedResult.nextState
        )
    }

    private fun createFallbackDirection(
        section: ArenaEdgeSection
    ): Vector2 {
        return when (section) {
            ArenaEdgeSection.TOP -> Vector2(0f, 1f)
            ArenaEdgeSection.BOTTOM -> Vector2(0f, -1f)
            ArenaEdgeSection.LEFT_UPPER,
            ArenaEdgeSection.LEFT_LOWER -> Vector2(1f, 0f)

            ArenaEdgeSection.RIGHT_UPPER,
            ArenaEdgeSection.RIGHT_LOWER -> Vector2(-1f, 0f)
        }
    }

    private data class ProjectilePath(
        val startPosition: Vector2,
        val targetPosition: Vector2
    )

    private data class RandomProjectileResult(
        val projectile: Projectile,
        val nextRandomState: BattleRandomState
    )
}