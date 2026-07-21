package ru.landilf.hellofbullets.domain.engine.battle.survival

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.landilf.hellofbullets.domain.engine.battle.common.ProjectileFactory
import ru.landilf.hellofbullets.domain.engine.battle.common.random.BattleRandomGenerator
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackPattern
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackWave
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ProjectileType
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.ArenaEdgeSection
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackEmitter
import ru.landilf.hellofbullets.domain.model.battle.common.attackpattern.AttackEmitterState
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.ProjectileGenerationState
import ru.landilf.hellofbullets.domain.model.battle.common.random.BattleRandomState
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWavePhase
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWaveState
import ru.landilf.hellofbullets.domain.model.common.FloatRange
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize

class SurvivalWaveUpdaterTest {
    private val fieldSize = GameFieldSize(
        width = 100f,
        height = 160f
    )

    @Test
    fun `does not spawn volley before interval ends`() {
        val emitter = createEmitter(
            patternOptions = listOf(
                createPattern(
                    id = 1L,
                    projectileCount = 2
                )
            ),
            volleyIntervalMs = 1_000
        )
        val wave = createWave(
            id = 1L,
            emitters = listOf(emitter),
            durationMs = 3_000,
            breakDurationMs = 1_000
        )
        val initialState = createWaveState(
            waves = listOf(wave),
            timeUntilPhaseEndMs = 3_000,
            emitterStates = listOf(
                AttackEmitterState(
                    timeUntilNextVolleyMs = 1_000
                )
            )
        )

        val result = createUpdater().update(
            waveState = initialState,
            deltaTimeMs = 400,
            initialGenerationState = createGenerationState(),
            fieldSize = fieldSize
        )
        val updatedState = requireNotNull(result.waveState)

        assertTrue(result.spawnedProjectiles.isEmpty())
        assertEquals(2_600, updatedState.timeUntilPhaseEndMs)
        assertEquals(600, updatedState.emitterStates.single().timeUntilNextVolleyMs)
        assertEquals(SurvivalWavePhase.ACTIVE, updatedState.phase)
    }

    @Test
    fun `spawns volley and resets emitter timer`() {
        val emitter = createEmitter(
            patternOptions = listOf(
                createPattern(
                    id = 1L,
                    projectileCount = 2
                )
            ),
            volleyIntervalMs = 500
        )
        val wave = createWave(
            id = 1L,
            emitters = listOf(emitter),
            durationMs = 3_000,
            breakDurationMs = 1_000
        )
        val initialState = createWaveState(
            waves = listOf(wave),
            timeUntilPhaseEndMs = 3_000,
            emitterStates = listOf(
                AttackEmitterState(
                    timeUntilNextVolleyMs = 500
                )
            )
        )

        val result = createUpdater().update(
            waveState = initialState,
            deltaTimeMs = 500,
            initialGenerationState = createGenerationState(
                nextProjectileId = 10L
            ),
            fieldSize = fieldSize
        )
        val updatedState = requireNotNull(result.waveState)

        assertEquals(
            listOf(10L, 11L),
            result.spawnedProjectiles.map { it.id }
        )
        assertEquals(2_500, updatedState.timeUntilPhaseEndMs)
        assertEquals(500, updatedState.emitterStates.single().timeUntilNextVolleyMs)
    }

    @Test
    fun `spawns volleys from all emitters when their timers end together`() {
        val firstEmitter = createEmitter(
            patternOptions = listOf(
                createPattern(
                    id = 1L,
                    projectileCount = 2
                )
            ),
            volleyIntervalMs = 500
        )
        val secondEmitter = createEmitter(
            patternOptions = listOf(
                createPattern(
                    id = 2L,
                    projectileCount = 3
                )
            ),
            volleyIntervalMs = 700
        )
        val wave = createWave(
            id = 1L,
            emitters = listOf(firstEmitter, secondEmitter),
            durationMs = 3_000,
            breakDurationMs = 1_000
        )
        val initialState = createWaveState(
            waves = listOf(wave),
            timeUntilPhaseEndMs = 3_000,
            emitterStates = listOf(
                AttackEmitterState(
                    timeUntilNextVolleyMs = 500
                ),
                AttackEmitterState(
                    timeUntilNextVolleyMs = 500
                )
            )
        )

        val result = createUpdater().update(
            waveState = initialState,
            deltaTimeMs = 500,
            initialGenerationState = createGenerationState(),
            fieldSize = fieldSize
        )
        val updatedState = requireNotNull(result.waveState)

        assertEquals(5, result.spawnedProjectiles.size)
        assertEquals(
            listOf(0L, 1L, 2L, 3L, 4L),
            result.spawnedProjectiles.map { it.id }
        )
        assertEquals(
            listOf(500, 700),
            updatedState.emitterStates.map { it.timeUntilNextVolleyMs }
        )
    }

    @Test
    fun `spawns volley only from emitter whose timer ends`() {
        val firstEmitter = createEmitter(
            patternOptions = listOf(
                createPattern(
                    id = 1L,
                    projectileCount = 2
                )
            ),
            volleyIntervalMs = 500
        )
        val secondEmitter = createEmitter(
            patternOptions = listOf(
                createPattern(
                    id = 2L,
                    projectileCount = 3
                )
            ),
            volleyIntervalMs = 700
        )
        val wave = createWave(
            id = 1L,
            emitters = listOf(firstEmitter, secondEmitter),
            durationMs = 3_000,
            breakDurationMs = 1_000
        )
        val initialState = createWaveState(
            waves = listOf(wave),
            timeUntilPhaseEndMs = 3_000,
            emitterStates = listOf(
                AttackEmitterState(
                    timeUntilNextVolleyMs = 500
                ),
                AttackEmitterState(
                    timeUntilNextVolleyMs = 700
                )
            )
        )

        val result = createUpdater().update(
            waveState = initialState,
            deltaTimeMs = 500,
            initialGenerationState = createGenerationState(),
            fieldSize = fieldSize
        )
        val updatedState = requireNotNull(result.waveState)

        assertEquals(2, result.spawnedProjectiles.size)
        assertEquals(
            listOf(500, 200),
            updatedState.emitterStates.map { it.timeUntilNextVolleyMs }
        )
    }

    @Test
    fun `starts break without spawning volleys when wave duration ends`() {
        val emitter = createEmitter(
            patternOptions = listOf(
                createPattern(
                    id = 1L,
                    projectileCount = 2
                )
            ),
            volleyIntervalMs = 500
        )
        val wave = createWave(
            id = 1L,
            emitters = listOf(emitter),
            durationMs = 3_000,
            breakDurationMs = 1_000
        )
        val initialState = createWaveState(
            waves = listOf(wave),
            timeUntilPhaseEndMs = 500,
            emitterStates = listOf(
                AttackEmitterState(
                    timeUntilNextVolleyMs = 500
                )
            )
        )

        val result = createUpdater().update(
            waveState = initialState,
            deltaTimeMs = 500,
            initialGenerationState = createGenerationState(),
            fieldSize = fieldSize
        )
        val updatedState = requireNotNull(result.waveState)

        assertTrue(result.spawnedProjectiles.isEmpty())
        assertEquals(SurvivalWavePhase.BREAK, updatedState.phase)
        assertEquals(1_000, updatedState.timeUntilPhaseEndMs)
    }

    @Test
    fun `starts another wave after break and excludes current wave`() {
        val firstWave = createWave(
            id = 1L,
            emitters = listOf(
                createEmitter(
                    patternOptions = listOf(
                        createPattern(
                            id = 1L,
                            projectileCount = 2,
                        )
                    ),
                    volleyIntervalMs = 500
                )
            ),
            durationMs = 3_000,
            breakDurationMs = 1_000
        )
        val secondWave = createWave(
            id = 2L,
            emitters = listOf(
                createEmitter(
                    patternOptions = listOf(
                        createPattern(
                            id = 2L,
                            projectileCount = 3,
                        )
                    ),
                    volleyIntervalMs = 700,
                    initialDelayMs = 300
                )
            ),
            durationMs = 4_000,
            breakDurationMs = 1_500
        )
        val initialState = createWaveState(
            waves = listOf(firstWave, secondWave),
            phase = SurvivalWavePhase.BREAK,
            timeUntilPhaseEndMs = 1_000,
            emitterStates = listOf(
                AttackEmitterState(
                    timeUntilNextVolleyMs = 500
                )
            )
        )

        val result = createUpdater().update(
            waveState = initialState,
            deltaTimeMs = 1_000,
            initialGenerationState = createGenerationState(),
            fieldSize = fieldSize
        )
        val updatedState = requireNotNull(result.waveState)

        assertEquals(SurvivalWavePhase.ACTIVE, updatedState.phase)
        assertEquals(1, updatedState.currentWaveIndex)
        assertEquals(4_000, updatedState.timeUntilPhaseEndMs)
        assertEquals(
            listOf(300),
            updatedState.emitterStates.map { it.timeUntilNextVolleyMs }
        )
    }

    @Test
    fun `selects pattern from emitter options using random state`() {
        val firstPattern = createPattern(
            id = 1L,
            projectileCount = 2
        )
        val secondPattern = createPattern(
            id = 2L,
            projectileCount = 3
        )
        val generationState = createGenerationState(
            seed = 123L
        )
        val emitter = createEmitter(
            patternOptions = listOf(firstPattern, secondPattern),
            volleyIntervalMs = 500
        )
        val wave = createWave(
            id = 1L,
            emitters = listOf(emitter),
            durationMs = 3_000,
            breakDurationMs = 1_000
        )
        val initialState = createWaveState(
            waves = listOf(wave),
            timeUntilPhaseEndMs = 3_000,
            emitterStates = listOf(
                AttackEmitterState(
                    timeUntilNextVolleyMs = 500
                )
            )
        )

        val expectedPattern = BattleRandomGenerator().pick(
            randomState = generationState.randomState,
            values = listOf(firstPattern, secondPattern)
        ).value

        val result = createUpdater().update(
            waveState = initialState,
            deltaTimeMs = 500,
            initialGenerationState = generationState,
            fieldSize = fieldSize
        )

        assertEquals(
            expectedPattern.projectileCount,
            result.spawnedProjectiles.size
        )
    }

    @Test
    fun `selects next wave from available options using random state`() {
        val firstWave = createWave(
            id = 1L,
            emitters = listOf(
                createEmitter(
                    patternOptions = listOf(
                        createPattern(
                            id = 1L,
                            projectileCount = 1
                        )
                    ),
                    volleyIntervalMs = 500,
                    initialDelayMs = 100
                )
            ),
            durationMs = 3_000,
            breakDurationMs = 1_000
        )
        val secondWave = createWave(
            id = 2L,
            emitters = listOf(
                createEmitter(
                    patternOptions = listOf(
                        createPattern(
                            id = 2L,
                            projectileCount = 1
                        )
                    ),
                    volleyIntervalMs = 500,
                    initialDelayMs = 200
                )
            ),
            durationMs = 4_000,
            breakDurationMs = 1_000
        )
        val thirdWave = createWave(
            id = 3L,
            emitters = listOf(
                createEmitter(
                    patternOptions = listOf(
                        createPattern(
                            id = 3L,
                            projectileCount = 1
                        )
                    ),
                    volleyIntervalMs = 500,
                    initialDelayMs = 300
                )
            ),
            durationMs = 5_000,
            breakDurationMs = 1_000
        )
        val generationState = createGenerationState(
            seed = 123L
        )
        val initialState = createWaveState(
            waves = listOf(firstWave, secondWave, thirdWave),
            phase = SurvivalWavePhase.BREAK,
            timeUntilPhaseEndMs = 1_000,
            emitterStates = listOf(
                AttackEmitterState(
                    timeUntilNextVolleyMs = 100
                )
            )
        )

        val expectedSelection = BattleRandomGenerator().pick(
            randomState = generationState.randomState,
            values = listOf(1, 2)
        )

        val result = createUpdater().update(
            waveState = initialState,
            deltaTimeMs = 1_000,
            initialGenerationState = generationState,
            fieldSize = fieldSize
        )
        val updatedResult = requireNotNull(result.waveState)

        assertEquals(expectedSelection.value, updatedResult.currentWaveIndex)
        assertEquals(
            listOf(
                if (expectedSelection.value == 1) 200 else 300
            ),
            updatedResult.emitterStates.map { it.timeUntilNextVolleyMs }
        )
        assertEquals(
            expectedSelection.nextState,
            result.nextGenerationState.randomState
        )
    }

    private fun createUpdater(): SurvivalWaveUpdater {
        val randomGenerator = BattleRandomGenerator()

        return SurvivalWaveUpdater(
            projectileFactory = ProjectileFactory(
                randomGenerator = randomGenerator
            ),
            randomGenerator = randomGenerator
        )
    }

    private fun createGenerationState(
        nextProjectileId: Long = 0L,
        seed: Long = 1L
    ): ProjectileGenerationState {
        return ProjectileGenerationState(
            nextProjectileId = nextProjectileId,
            randomState = BattleRandomState(seed)
        )
    }

    private fun createEmitter(
        patternOptions: List<AttackPattern>,
        volleyIntervalMs: Int,
        initialDelayMs: Int = volleyIntervalMs
    ): AttackEmitter {
        return AttackEmitter(
            patternOptions = patternOptions,
            volleyIntervalMs = volleyIntervalMs,
            initialDelayMs = initialDelayMs
        )
    }

    private fun createWave(
        id: Long,
        emitters: List<AttackEmitter>,
        durationMs: Int,
        breakDurationMs: Int
    ): AttackWave {
        return AttackWave(
            id = id,
            emitters = emitters,
            durationMs = durationMs,
            breakDurationMs = breakDurationMs
        )
    }

    private fun createPattern(
        id: Long,
        projectileCount: Int
    ): AttackPattern {
        return AttackPattern(
            id = id,
            projectileType = ProjectileType.BULLET,
            spawnSection = ArenaEdgeSection.TOP,
            targetSections = listOf(ArenaEdgeSection.BOTTOM),
            projectileCount = projectileCount,
            projectileSpeedRange = FloatRange(
                min = 10f,
                max = 10f
            ),
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
        emitterStates: List<AttackEmitterState>
    ): SurvivalWaveState {
        return SurvivalWaveState(
            waves = waves,
            currentWaveIndex = currentWaveIndex,
            phase = phase,
            timeUntilPhaseEndMs = timeUntilPhaseEndMs,
            emitterStates = emitterStates
        )
    }
}
