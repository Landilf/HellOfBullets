package ru.landilf.hellofbullets.domain.engine.battle.common

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserPhase
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserProjectile
import ru.landilf.hellofbullets.domain.model.common.Vector2
import ru.landilf.hellofbullets.domain.model.player.PlayerRuntimeState

class PlayerCollisionCheckerTest {
    private val collisionChecker = PlayerCollisionChecker()

    @Test
    fun `does not collide with laser during warning phase`() {
        assertFalse(
            collisionChecker.hasCollision(
                player = createPlayer(),
                projectiles = listOf(
                    createLaser(
                        phase = LaserPhase.WARNING,
                        remainingWarningMs = 500
                    )
                )
            )
        )
    }

    @Test
    fun `collides with laser during active phase`() {
        assertTrue(
            collisionChecker.hasCollision(
                player = createPlayer(),
                projectiles = listOf(
                    createLaser(
                        phase = LaserPhase.ACTIVE,
                        remainingWarningMs = 0
                    )
                )
            )
        )
    }

    private fun createPlayer(): PlayerRuntimeState {
        return PlayerRuntimeState(
            currentHp = 1,
            position = Vector2(
                x = 50f,
                y = 50f
            ),
            hitRadius = 2f,
            isAlive = true
        )
    }

    private fun createLaser(
        phase: LaserPhase,
        remainingWarningMs: Int
    ): LaserProjectile {
        return LaserProjectile(
            id = 1L,
            damage = 1,
            hitRadius = 1f,
            remainingLifetimeMs = 1_000,
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