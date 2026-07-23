package ru.landilf.hellofbullets.domain.engine.battle.common

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserPhase
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.RocketProjectile
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize
import ru.landilf.hellofbullets.domain.model.common.Vector2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ProjectileMovementUpdaterTest {
    private val updater = ProjectileMovementUpdater()
    private val fieldSize = GameFieldSize(
        width = 100f,
        height = 160f
    )

    @Test
    fun `keeps laser in warning phase before warning ends`() {
        val result = updater.update(
            projectiles = listOf(
                createLaser(
                    remainingLifetimeMs = 1_500,
                    phase = LaserPhase.WARNING,
                    remainingWarningMs = 500
                )
            ),
            deltaTimeMs = 200,
            fieldSize = GameFieldSize(
                width = 100f,
                height = 160f
            ),
            playerPosition = Vector2(
                x = 50f,
                y = 50f
            )
        )

        val laser = result.single() as LaserProjectile

        assertEquals(LaserPhase.WARNING, laser.phase)
        assertEquals(300, laser.remainingWarningMs)
    }

    @Test
    fun `activates laser when warning ends`() {
        val result = updater.update(
            projectiles = listOf(
                createLaser(
                    remainingLifetimeMs = 1_200,
                    phase = LaserPhase.WARNING,
                    remainingWarningMs = 200
                )
            ),
            deltaTimeMs = 200,
            fieldSize = fieldSize,
            playerPosition = Vector2(
                x = 50f,
                y = 50f
            )
        )

        val laser = result.single() as LaserProjectile

        assertEquals(LaserPhase.ACTIVE, laser.phase)
        assertEquals(0, laser.remainingWarningMs)
        assertEquals(1_000, laser.remainingLifetimeMs)
    }

    @Test
    fun `rotates rocket no more than maximum turn angle`() {
        val rocket = updateRocket(
            rocket = createRocket(
                velocity = Vector2(
                    x = 10f,
                    y = 0f
                ),
                remainingHomingTimeMs = 1_000
            ),
            deltaTimeMs = 500
        )

        val expectedAngle = (Math.PI / 4.0).toFloat()

        assertEquals(
            10f * cos(expectedAngle),
            rocket.velocity.x,
            EPSILON
        )
        assertEquals(
            10f * sin(expectedAngle),
            rocket.velocity.y,
            EPSILON
        )
        assertEquals(500, rocket.remainingHomingTimeMs)

        val speed = sqrt(
            rocket.velocity.x * rocket.velocity.x + rocket.velocity.y * rocket.velocity.y
        )
        assertEquals(10f, speed, EPSILON)
    }

    @Test
    fun `uses only remaining homing time when tick crosses homing end`() {
        val rocket = updateRocket(
            rocket = createRocket(
                velocity = Vector2(
                    x = 10f,
                    y = 0f
                ),
                remainingHomingTimeMs = 200
            ),
            deltaTimeMs = 500
        )

        val expectedAngle = (Math.PI / 10.0).toFloat()

        assertEquals(
            10f * cos(expectedAngle),
            rocket.velocity.x,
            EPSILON
        )
        assertEquals(
            10f * sin(expectedAngle),
            rocket.velocity.y,
            EPSILON
        )
        assertEquals(0, rocket.remainingHomingTimeMs)
        assertEquals(
            20f + 10f * cos(expectedAngle) * 0.5f,
            rocket.position.x,
            EPSILON
        )
        assertEquals(
            20f + 10f * sin(expectedAngle) * 0.5f,
            rocket.position.y,
            EPSILON
        )
    }

    @Test
    fun `keeps rocket direction after homing ends`() {
        val rocket = updateRocket(
            rocket = createRocket(
                velocity = Vector2(
                    x = 10f,
                    y = 0f
                ),
                remainingHomingTimeMs = 0
            ),
            deltaTimeMs = 500
        )

        assertEquals(10f, rocket.velocity.x, EPSILON)
        assertEquals(0f, rocket.velocity.y, EPSILON)
        assertEquals(25f, rocket.position.x, EPSILON)
        assertEquals(20f, rocket.position.y, EPSILON)
    }

    private fun createLaser(
        remainingLifetimeMs: Int,
        phase: LaserPhase,
        remainingWarningMs: Int
    ): LaserProjectile {
        return LaserProjectile(
            id = 1L,
            damage = 1,
            hitRadius = 1f,
            remainingLifetimeMs = remainingLifetimeMs,
            startPosition = Vector2(
                x = 0f,
                y = 50f
            ),
            endPosition = Vector2(
                x = 100f,
                y = 50f
            ),
            phase = phase,
            remainingWarningMs = remainingWarningMs
        )
    }

    private fun createRocket(
        velocity: Vector2,
        remainingHomingTimeMs: Int
    ): RocketProjectile {
        return RocketProjectile(
            id = 1L,
            damage = 1,
            hitRadius = 1f,
            remainingLifetimeMs = 5_000,
            position = Vector2(
                x = 20f,
                y = 20f
            ),
            velocity = velocity,
            remainingHomingTimeMs = remainingHomingTimeMs,
            maxTurnRateRadiansPerSecond = (PI / 2.0).toFloat()
        )
    }

    private fun updateRocket(
        rocket: RocketProjectile,
        deltaTimeMs: Int
    ): RocketProjectile {
        return updater.update(
            projectiles = listOf(rocket),
            deltaTimeMs = deltaTimeMs,
            fieldSize = fieldSize,
            playerPosition = Vector2(
                x = 20f,
                y = 100f
            )
        ).single() as RocketProjectile
    }

    companion object {
        const val EPSILON = 0.0001f
    }
}