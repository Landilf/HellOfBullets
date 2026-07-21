package ru.landilf.hellofbullets.domain.engine.battle.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.landilf.hellofbullets.domain.engine.battle.common.random.BattleRandomGenerator
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ArenaEdgeSection
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackPattern
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ProjectileType
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.BulletProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.LaserProjectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.ProjectileGenerationState
import ru.landilf.hellofbullets.domain.model.battle.common.random.BattleRandomState
import ru.landilf.hellofbullets.domain.model.common.FloatRange
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize
import kotlin.math.sqrt

class ProjectileFactoryTest {
    private val factory = ProjectileFactory(
        randomGenerator = BattleRandomGenerator()
    )

    private val fieldSize = GameFieldSize(
        width = 100f,
        height = 160f
    )

    @Test
    fun `creates identical volley for identical generation state`() {
        val pattern = createPattern(
            projectileType = ProjectileType.BULLET,
            spawnSection = ArenaEdgeSection.TOP,
            targetSections = listOf(ArenaEdgeSection.BOTTOM),
            projectileCount = 3,
            speedRange = FloatRange(40f, 80f)
        )
        val generationState = createGenerationState(
            nextProjectileId = 10L,
            seed = 123L
        )

        val firstResult = factory.createVolley(
            pattern = pattern,
            generationState = generationState,
            fieldSize = fieldSize
        )
        val secondResult = factory.createVolley(
            pattern = pattern,
            generationState = generationState,
            fieldSize = fieldSize
        )

        assertEquals(firstResult, secondResult)
    }

    @Test
    fun `creates bullets inside top section with speed from configured range`() {
        val speedRange = FloatRange(40f, 80f)
        val pattern = createPattern(
            projectileType = ProjectileType.BULLET,
            spawnSection = ArenaEdgeSection.TOP,
            targetSections = listOf(ArenaEdgeSection.BOTTOM),
            projectileCount = 5,
            speedRange = speedRange
        )

        val result = factory.createVolley(
            pattern = pattern,
            generationState = createGenerationState(
                nextProjectileId = 0L,
                seed = 456L
            ),
            fieldSize = fieldSize
        )
        val bullets = result.projectiles.filterIsInstance<BulletProjectile>()

        assertEquals(5, bullets.size)

        bullets.forEach { bullet ->
            assertEquals(0f, bullet.position.y, 0f)
            assertTrue(bullet.position.x in 0f..fieldSize.width)

            val speed = sqrt(
                bullet.velocity.x * bullet.velocity.x + bullet.velocity.y * bullet.velocity.y
            )

            assertTrue(speed >= speedRange.min - EPSILON)
            assertTrue(speed <= speedRange.max + EPSILON)
        }
    }

    @Test
    fun `creates laser from top section to selected left lower section`() {
        val pattern = createPattern(
            projectileType = ProjectileType.LASER,
            spawnSection = ArenaEdgeSection.TOP,
            targetSections = listOf(ArenaEdgeSection.LEFT_LOWER),
            projectileCount = 1,
            speedRange = FloatRange(0f, 0f)
        )

        val result = factory.createVolley(
            pattern = pattern,
            generationState = createGenerationState(
                nextProjectileId = 0L,
                seed = 789L
            ),
            fieldSize = fieldSize
        )
        val laser = result.projectiles.single() as LaserProjectile

        assertEquals(0f, laser.startPosition.y, 0f)
        assertTrue(laser.startPosition.x in 0f..fieldSize.width)

        assertEquals(0f, laser.endPosition.x, 0f)
        assertTrue(laser.endPosition.y in fieldSize.height / 2f..fieldSize.height)
    }

    private fun createPattern(
        projectileType: ProjectileType,
        spawnSection: ArenaEdgeSection,
        targetSections: List<ArenaEdgeSection>,
        projectileCount: Int,
        speedRange: FloatRange
    ): AttackPattern {
        return AttackPattern(
            id = 1L,
            projectileType = projectileType,
            spawnSection = spawnSection,
            targetSections = targetSections,
            projectileCount = projectileCount,
            projectileSpeedRange = speedRange,
            projectileDamage = 1,
            projectileHitRadius = 1f,
            projectileLifetimeMs = 5_000
        )
    }

    private fun createGenerationState(
        nextProjectileId: Long,
        seed: Long
    ): ProjectileGenerationState {
        return ProjectileGenerationState(
            nextProjectileId = nextProjectileId,
            randomState = BattleRandomState(
                seed = seed
            )
        )
    }

    private companion object {
        const val EPSILON = 0.0001f
    }
}