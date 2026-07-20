package ru.landilf.hellofbullets.domain.engine.battle.survival

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.landilf.hellofbullets.domain.engine.battle.common.ProjectileFactory
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackPattern
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackWave
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ProjectileType
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.SpawnZone
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWavePhase
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWaveState
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize

class SurvivalWaveUpdaterTest {
    private val fieldSize = GameFieldSize(
        width = 100f,
        height = 160f
    )

    @Test
    fun `does not spawn volley before interval ends`() {
        val wave = createWave(
            id = 1L,
            durationMs = 3_000,
            breakDurationMs = 1_000,
            patterns = listOf(
                createPattern(
                    id = 1L,
                    projectileCount = 2,
                    spawnIntervalMs = 1_000
                )
            )
        )
        val initialState = createWaveState(
            waves = listOf(wave),
            timeUntilPhaseEndMs = 3_000,
            timeUntilNextVolleyMs = 1_000
        )

        val result = createUpdater().update(
            waveState = initialState,
            deltaTimeMs = 400,
            initialProjectileId = 0L,
            fieldSize = fieldSize
        )
        val updatedState = requireNotNull(result.waveState)

        assertTrue(result.spawnedProjectiles.isEmpty())
        assertEquals(2_600, updatedState.timeUntilPhaseEndMs)
        assertEquals(600, updatedState.timeUntilNextVolleyMs)
        assertEquals(SurvivalWavePhase.ACTIVE, updatedState.phase)
    }

    @Test
    fun `spawns volley and switches to next pattern when interval ends`() {
        val firstPattern = createPattern(
            id = 1L,
            projectileCount = 2,
            spawnIntervalMs = 500
        )
        val secondPattern = createPattern(
            id = 2L,
            projectileCount = 3,
            spawnIntervalMs = 700
        )

        val wave = createWave(
            id = 1L,
            durationMs = 3_000,
            breakDurationMs = 1_000,
            patterns = listOf(firstPattern, secondPattern)
        )
        val initialState = createWaveState(
            waves = listOf(wave),
            timeUntilPhaseEndMs = 3_000,
            timeUntilNextVolleyMs = 500
        )

        val result = createUpdater().update(
            waveState = initialState,
            deltaTimeMs = 500,
            initialProjectileId = 10L,
            fieldSize = fieldSize
        )
        val updatedState = requireNotNull(result.waveState)

        assertEquals(2, result.spawnedProjectiles.size)
        assertEquals(
            listOf(10L, 11L),
            result.spawnedProjectiles.map { it.id }
        )
        assertEquals(2_500, updatedState.timeUntilPhaseEndMs)
        assertEquals(1, updatedState.currentPatternIndex)
        assertEquals(700, updatedState.timeUntilNextVolleyMs)
    }

    @Test
    fun `starts break without spawning volley when wave duration ends`() {
        val wave = createWave(
            id = 1L,
            durationMs = 3_000,
            breakDurationMs = 1_000,
            patterns = listOf(
                createPattern(
                    id = 1L,
                    projectileCount = 2,
                    spawnIntervalMs = 500
                )
            )
        )
        val initialState = createWaveState(
            waves = listOf(wave),
            timeUntilPhaseEndMs = 500,
            timeUntilNextVolleyMs = 500
        )

        val result = createUpdater().update(
            waveState = initialState,
            deltaTimeMs = 500,
            initialProjectileId = 0L,
            fieldSize = fieldSize
        )
        val updatedState = requireNotNull(result.waveState)

        assertTrue(result.spawnedProjectiles.isEmpty())
        assertEquals(SurvivalWavePhase.BREAK, updatedState.phase)
        assertEquals(1_000, updatedState.timeUntilPhaseEndMs)
    }

    @Test
    fun `starts next wave after break ends`() {
        val firstWave = createWave(
            id = 1L,
            durationMs = 3_000,
            breakDurationMs = 1_000,
            patterns = listOf(
                createPattern(
                    id = 1L,
                    projectileCount = 2,
                    spawnIntervalMs = 500
                )
            )
        )
        val secondWave = createWave(
            id = 2L,
            durationMs = 4_000,
            breakDurationMs = 1_500,
            patterns = listOf(
                createPattern(
                    id = 2L,
                    projectileCount = 3,
                    spawnIntervalMs = 700
                )
            )
        )
        val initialState = createWaveState(
            waves = listOf(firstWave, secondWave),
            phase = SurvivalWavePhase.BREAK,
            timeUntilPhaseEndMs = 1_000,
            timeUntilNextVolleyMs = 500
        )

        val result = createUpdater().update(
            waveState = initialState,
            deltaTimeMs = 1_000,
            initialProjectileId = 0L,
            fieldSize = fieldSize
        )
        val updatedState = requireNotNull(result.waveState)

        assertTrue(result.spawnedProjectiles.isEmpty())
        assertEquals(SurvivalWavePhase.ACTIVE, updatedState.phase)
        assertEquals(1, updatedState.currentWaveIndex)
        assertEquals(4_000, updatedState.timeUntilPhaseEndMs)
        assertEquals(0, updatedState.currentPatternIndex)
        assertEquals(700, updatedState.timeUntilNextVolleyMs)
    }

    @Test
    fun `process next wave volley with remaining time after wave and break end`() {
        val firstWave = createWave(
            id = 1L,
            durationMs = 3_000,
            breakDurationMs = 1_000,
            patterns = listOf(
                createPattern(
                    id = 1L,
                    projectileCount = 2,
                    spawnIntervalMs = 500
                )
            )
        )
        val secondWave = createWave(
            id = 2L,
            durationMs = 4_000,
            breakDurationMs = 1_500,
            patterns = listOf(
                createPattern(
                    id = 2L,
                    projectileCount = 3,
                    spawnIntervalMs = 500
                )
            )
        )
        val initialState = createWaveState(
            waves = listOf(firstWave, secondWave),
            phase = SurvivalWavePhase.ACTIVE,
            timeUntilPhaseEndMs = 500,
            timeUntilNextVolleyMs = 500
        )

        val result = createUpdater().update(
            waveState = initialState,
            deltaTimeMs = 2_000,
            initialProjectileId = 0L,
            fieldSize = fieldSize
        )
        val updatedState = requireNotNull(result.waveState)

        assertEquals(3, result.spawnedProjectiles.size)
        assertEquals(SurvivalWavePhase.ACTIVE, updatedState.phase)
        assertEquals(1, updatedState.currentWaveIndex)
        assertEquals(3_500, updatedState.timeUntilPhaseEndMs)
        assertEquals(0, updatedState.currentPatternIndex)
        assertEquals(500, updatedState.timeUntilNextVolleyMs)
    }

    @Test
    fun `assigns sequential ids to multiple volleys in one update`() {
        val wave = createWave(
            id = 1L,
            durationMs = 3_000,
            breakDurationMs = 1_000,
            patterns = listOf(
                createPattern(
                    id = 1L,
                    projectileCount = 2,
                    spawnIntervalMs = 500
                )
            )
        )
        val initialState = createWaveState(
            waves = listOf(wave),
            phase = SurvivalWavePhase.ACTIVE,
            timeUntilPhaseEndMs = 3_000,
            timeUntilNextVolleyMs = 500
        )

        val result = createUpdater().update(
            waveState = initialState,
            deltaTimeMs = 1_000,
            initialProjectileId = 20L,
            fieldSize = fieldSize
        )

        assertEquals(
            listOf(20L, 21L, 22L, 23L),
            result.spawnedProjectiles.map { it.id }
        )
        assertEquals(24L, result.nextProjectileId)
    }

    private fun createUpdater(): SurvivalWaveUpdater {
        return SurvivalWaveUpdater(
            projectileFactory = ProjectileFactory()
        )
    }

    private fun createWave(
        id: Long,
        durationMs: Int,
        breakDurationMs: Int,
        patterns: List<AttackPattern>
    ): AttackWave {
        return AttackWave(
            id = id,
            patterns = patterns,
            durationMs = durationMs,
            breakDurationMs = breakDurationMs
        )
    }

    private fun createPattern(
        id: Long,
        projectileCount: Int,
        spawnIntervalMs: Int
    ): AttackPattern {
        return AttackPattern(
            id = id,
            projectileType = ProjectileType.BULLET,
            spawnZone = SpawnZone.TOP,
            projectileCount = projectileCount,
            spawnIntervalMs = spawnIntervalMs,
            projectileSpeed = 10f,
            projectileDamage = 1,
            projectileHitRadius = 1f,
            projectileLifetimeMs = 5_000
        )
    }

    private fun createWaveState(
        waves: List<AttackWave>,
        currentWaveIndex: Int = 0,
        phase: SurvivalWavePhase = SurvivalWavePhase.ACTIVE,
        timeUntilPhaseEndMs: Int,
        currentPatternIndex: Int = 0,
        timeUntilNextVolleyMs: Int
    ): SurvivalWaveState {
        return SurvivalWaveState(
            waves = waves,
            currentWaveIndex = currentWaveIndex,
            phase = phase,
            timeUntilPhaseEndMs = timeUntilPhaseEndMs,
            currentPatternIndex = currentPatternIndex,
            timeUntilNextVolleyMs = timeUntilNextVolleyMs
        )
    }
}