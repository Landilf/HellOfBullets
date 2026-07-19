package ru.landilf.hellofbullets.domain.engine.battle.survival

import ru.landilf.hellofbullets.domain.engine.battle.common.ProjectileFactory
import ru.landilf.hellofbullets.domain.model.battle.common.projectile.Projectile
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
        fieldSize: GameFieldSize
    ): SurvivalWaveUpdateResult {
        val initialWaveState = waveState ?: return SurvivalWaveUpdateResult(
            waveState = null,
            spawnedProjectiles = emptyList()
        )

        if (deltaTimeMs <= 0) {
            return SurvivalWaveUpdateResult(
                waveState = initialWaveState,
                spawnedProjectiles = emptyList()
            )
        }

        var updatedWaveState = initialWaveState
        var remainingDeltaTimeMs = deltaTimeMs
        val spawnedProjectiles = mutableListOf<Projectile>()

        while (remainingDeltaTimeMs > 0) {
            when (updatedWaveState.phase) {
                SurvivalWavePhase.ACTIVE -> {
                    val updateResult = updateActivePhase(
                        waveState = updatedWaveState,
                        deltaTimeMs = remainingDeltaTimeMs,
                        fieldSize = fieldSize
                    )

                    updatedWaveState = updateResult.waveState
                    remainingDeltaTimeMs -= updateResult.usedDeltaTimeMs
                    spawnedProjectiles += updateResult.spawnedProjectiles
                }

                SurvivalWavePhase.BREAK -> {
                    val updateResult = updateBreakPhase(
                        waveState = updatedWaveState,
                        deltaTimeMs = remainingDeltaTimeMs
                    )

                    updatedWaveState = updateResult.waveState
                    remainingDeltaTimeMs -= updateResult.usedDeltaTimeMs
                }
            }
        }

        return SurvivalWaveUpdateResult(
            waveState = updatedWaveState,
            spawnedProjectiles = spawnedProjectiles
        )
    }

    private fun updateActivePhase(
        waveState: SurvivalWaveState,
        deltaTimeMs: Int,
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
                spawnedProjectiles = emptyList()
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
                spawnedProjectiles = emptyList()
            )
        }

        val currentPattern = currentWave.patterns[stateAtEvent.currentPatternIndex]
        val nextPatternIndex = (stateAtEvent.currentPatternIndex + 1) % currentWave.patterns.size
        val nextPattern = currentWave.patterns[nextPatternIndex]

        return WavePhaseUpdateResult(
            waveState = stateAtEvent.copy(
                currentPatternIndex = nextPatternIndex,
                timeUntilNextVolleyMs = nextPattern.spawnIntervalMs
            ),
            usedDeltaTimeMs = timeUntilNextEventMs,
            spawnedProjectiles = projectileFactory.createVolley(
                pattern = currentPattern,
                fieldSize = fieldSize
            )
        )
    }

    private fun updateBreakPhase(
        waveState: SurvivalWaveState,
        deltaTimeMs: Int
    ): WavePhaseUpdateResult {
        if (deltaTimeMs < waveState.timeUntilPhaseEndMs) {
            return WavePhaseUpdateResult(
                waveState = waveState.copy(
                    timeUntilPhaseEndMs = waveState.timeUntilPhaseEndMs - deltaTimeMs
                ),
                usedDeltaTimeMs = deltaTimeMs,
                spawnedProjectiles = emptyList()
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
            spawnedProjectiles = emptyList()
        )
    }

    private data class WavePhaseUpdateResult(
        val waveState: SurvivalWaveState,
        val usedDeltaTimeMs: Int,
        val spawnedProjectiles: List<Projectile>
    )
}