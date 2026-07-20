package ru.landilf.hellofbullets.domain.engine.battle.survival

import ru.landilf.hellofbullets.domain.engine.battle.common.ProjectileFactory
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.Projectile
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.ProjectileGenerationState
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWavePhase
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWaveState
import ru.landilf.hellofbullets.domain.model.battle.survival.SurvivalWaveUpdateResult
import ru.landilf.hellofbullets.domain.model.common.GameFieldSize
import javax.inject.Inject

class SurvivalWaveUpdater @Inject constructor(
    private val projectileFactory: ProjectileFactory
) {
    fun update(
        waveState: SurvivalWaveState?,
        deltaTimeMs: Int,
        initialGenerationState: ProjectileGenerationState,
        fieldSize: GameFieldSize
    ): SurvivalWaveUpdateResult {
        val initialWaveState = waveState ?: return SurvivalWaveUpdateResult(
            waveState = null,
            spawnedProjectiles = emptyList(),
            nextGenerationState = initialGenerationState
        )

        if (deltaTimeMs <= 0) {
            return SurvivalWaveUpdateResult(
                waveState = initialWaveState,
                spawnedProjectiles = emptyList(),
                nextGenerationState = initialGenerationState
            )
        }

        var updatedWaveState = initialWaveState
        var generationState = initialGenerationState
        var remainingDeltaTimeMs = deltaTimeMs
        val spawnedProjectiles = mutableListOf<Projectile>()

        while (remainingDeltaTimeMs > 0) {
            when (updatedWaveState.phase) {
                SurvivalWavePhase.ACTIVE -> {
                    val updateResult = updateActivePhase(
                        waveState = updatedWaveState,
                        deltaTimeMs = remainingDeltaTimeMs,
                        generationState = generationState,
                        fieldSize = fieldSize
                    )

                    updatedWaveState = updateResult.waveState
                    generationState = updateResult.nextGenerationState
                    remainingDeltaTimeMs -= updateResult.usedDeltaTimeMs
                    spawnedProjectiles += updateResult.spawnedProjectiles
                }

                SurvivalWavePhase.BREAK -> {
                    val updateResult = updateBreakPhase(
                        waveState = updatedWaveState,
                        deltaTimeMs = remainingDeltaTimeMs,
                        generationState = generationState
                    )

                    updatedWaveState = updateResult.waveState
                    generationState = updateResult.nextGenerationState
                    remainingDeltaTimeMs -= updateResult.usedDeltaTimeMs
                    spawnedProjectiles += updateResult.spawnedProjectiles
                }
            }
        }

        return SurvivalWaveUpdateResult(
            waveState = updatedWaveState,
            spawnedProjectiles = spawnedProjectiles,
            nextGenerationState = generationState
        )
    }

    private fun updateActivePhase(
        waveState: SurvivalWaveState,
        deltaTimeMs: Int,
        generationState: ProjectileGenerationState,
        fieldSize: GameFieldSize
    ): WavePhaseUpdateResult {
        val currentWave = waveState.currentWave
        val timeUntilNextEventMs = minOf(
            a = waveState.timeUntilPhaseEndMs,
            b = waveState.timeUntilNextVolleyMs
        )

        if (deltaTimeMs < timeUntilNextEventMs) {
            return WavePhaseUpdateResult(
                waveState = waveState.copy(
                    timeUntilPhaseEndMs = waveState.timeUntilPhaseEndMs - deltaTimeMs,
                    timeUntilNextVolleyMs = waveState.timeUntilNextVolleyMs - deltaTimeMs
                ),
                usedDeltaTimeMs = deltaTimeMs,
                spawnedProjectiles = emptyList(),
                nextGenerationState = generationState
            )
        }

        val stateAtEvent = waveState.copy(
            timeUntilPhaseEndMs = waveState.timeUntilPhaseEndMs - timeUntilNextEventMs,
            timeUntilNextVolleyMs = waveState.timeUntilNextVolleyMs - timeUntilNextEventMs
        )

        if (stateAtEvent.timeUntilPhaseEndMs == 0) {
            return WavePhaseUpdateResult(
                waveState = stateAtEvent.copy(
                    phase = SurvivalWavePhase.BREAK,
                    timeUntilPhaseEndMs = currentWave.breakDurationMs
                ),
                usedDeltaTimeMs = timeUntilNextEventMs,
                spawnedProjectiles = emptyList(),
                nextGenerationState = generationState
            )
        }

        val currentPattern = currentWave.patterns[stateAtEvent.currentPatternIndex]
        val nextPatternIndex = (stateAtEvent.currentPatternIndex + 1) % currentWave.patterns.size
        val nextPattern = currentWave.patterns[nextPatternIndex]

        val projectileCreationResult = projectileFactory.createVolley(
            pattern = currentPattern,
            generationState = generationState,
            fieldSize = fieldSize
        )

        return WavePhaseUpdateResult(
            waveState = stateAtEvent.copy(
                currentPatternIndex = nextPatternIndex,
                timeUntilNextVolleyMs = nextPattern.spawnIntervalMs
            ),
            usedDeltaTimeMs = timeUntilNextEventMs,
            spawnedProjectiles = projectileCreationResult.projectiles,
            nextGenerationState = projectileCreationResult.nextGenerationState
        )
    }

    private fun updateBreakPhase(
        waveState: SurvivalWaveState,
        deltaTimeMs: Int,
        generationState: ProjectileGenerationState
    ): WavePhaseUpdateResult {
        if (deltaTimeMs < waveState.timeUntilPhaseEndMs) {
            return WavePhaseUpdateResult(
                waveState = waveState.copy(
                    timeUntilPhaseEndMs = waveState.timeUntilPhaseEndMs - deltaTimeMs
                ),
                usedDeltaTimeMs = deltaTimeMs,
                spawnedProjectiles = emptyList(),
                nextGenerationState = generationState
            )
        }

        val nextWaveIndex = (waveState.currentWaveIndex + 1) % waveState.waves.size
        val nextWave = waveState.waves[nextWaveIndex]
        val firstPattern = nextWave.patterns.first()

        return WavePhaseUpdateResult(
            waveState = waveState.copy(
                currentWaveIndex = nextWaveIndex,
                phase = SurvivalWavePhase.ACTIVE,
                timeUntilPhaseEndMs = nextWave.durationMs,
                currentPatternIndex = 0,
                timeUntilNextVolleyMs = firstPattern.spawnIntervalMs
            ),
            usedDeltaTimeMs = waveState.timeUntilPhaseEndMs,
            spawnedProjectiles = emptyList(),
            nextGenerationState = generationState
        )
    }

    private data class WavePhaseUpdateResult(
        val waveState: SurvivalWaveState,
        val usedDeltaTimeMs: Int,
        val spawnedProjectiles: List<Projectile>,
        val nextGenerationState: ProjectileGenerationState
    )
}