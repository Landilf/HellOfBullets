package ru.landilf.hellofbullets.domain.engine.battle.common

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserPhase
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserProjectile
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize
import ru.landilf.hellofbullets.domain.model.common.Vector2

class ProjectileMovementUpdaterTest {
    private val updater = ProjectileMovementUpdater()

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
            fieldSize = GameFieldSize(
                width = 100f,
                height = 160f
            )
        )

        val laser = result.single() as LaserProjectile

        assertEquals(LaserPhase.ACTIVE, laser.phase)
        assertEquals(0, laser.remainingWarningMs)
        assertEquals(1_000, laser.remainingLifetimeMs)
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
}